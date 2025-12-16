package server;

import shared.packet.MovePacket;
import shared.packet.SyncPacket;

import static client.game.player.Unit.UNIT_SIZE_WIDTH;
import static client.game.player.Unit.UNIT_SIZE_HEIGHT;
import static client.game.obstacle.Obstacle.OBSTACLE_SIZE;

public class GameEngine {

    private int unitX = WorldConfig.SPAWN_X;
    private int unitY = WorldConfig.SPAWN_Y;
    private int vx = 0;
    private int vy = 0;
    private int lastDir = MovePacket.DOWN;

    private int obstarcleX_x = 390;
    private int obstarcleY_y = 210;
    private int OBSTACLE_X_FIXED_Y = 280;
    private int OBSTACLE_Y_FIXED_X = 1120;
    private int oX = 0;
    private int oY = 0;

    private final CollisionMask collisionMask = CollisionMask.getInstance();

    // ====== 타이머/종료 ======
    private long startAtMs = -1;
    private boolean finished = false;
    private long clearTimeMs = -1;

    // ====== ✅ 체크포인트 상태 ======
    // 마지막으로 밟은 체크포인트 인덱스 (-1이면 아직 하나도 안 밟음)
    private int lastCheckpointIdx = -1;

    // 현재 리스폰 위치(체크포인트 밟으면 갱신)
    private int respawnX = WorldConfig.SPAWN_X;
    private int respawnY = WorldConfig.SPAWN_Y;

    private ServerWindow window;
    private String roomTitle;

    public GameEngine(ServerWindow window, String roomTitle) {
        this.window = window;
        this.roomTitle = roomTitle;
    }

    private long now() { return System.currentTimeMillis(); }

    private long elapsedMsRaw() {
        if (startAtMs < 0) return 0;
        return now() - startAtMs;
    }

    private long elapsedMsForSync() {
        if (finished) return clearTimeMs;
        return elapsedMsRaw();
    }

    public void scheduleStartAfter(long delayMs) {
        startAtMs = now() + delayMs;
        finished = false;
        clearTimeMs = -1;

        // ✅ 새 게임 시작이면 체크포인트/리스폰도 초기화
        lastCheckpointIdx = -1;
        respawnX = WorldConfig.SPAWN_X;
        respawnY = WorldConfig.SPAWN_Y;
        unitX = respawnX;
        unitY = respawnY;
        vx = 0; vy = 0;
        lastDir = MovePacket.DOWN;
    }

    public void start() {
        oX = 1;
        oY = 1;
        if (startAtMs < 0) startAtMs = now();
    }

    public void applyMove(MovePacket packet) {
        if (finished) return; // ✅ 끝나면 입력 무시

        int dir = packet.getDirection();
        switch (dir) {
            case MovePacket.STOP -> { vx = 0; vy = 0; }
            case MovePacket.LEFT -> { vx = -1; vy = 0; }
            case MovePacket.RIGHT -> { vx = 1; vy = 0; }
            case MovePacket.UP -> { vx = 0; vy = -1; }
            case MovePacket.DOWN -> { vx = 0; vy = 1; }
        }
        if (dir != MovePacket.STOP) lastDir = dir;
    }

    public SyncPacket step() {
        long eRaw = elapsedMsRaw();

        // finished면 시간 고정해서 계속 내려줌
        if (finished) {
            return new SyncPacket(unitX, unitY, obstarcleX_x, obstarcleY_y, lastDir, elapsedMsForSync(), true);
        }

        // 시작 전 카운트다운: 움직임/충돌 진행 X
        if (eRaw < 0) {
            vx = 0; vy = 0; // ✅ 미리 눌러둔 입력 방지
            return new SyncPacket(unitX, unitY, obstarcleX_x, obstarcleY_y, lastDir, eRaw, false);
        }

        // ====== 기존 이동/충돌 ======
        int speed = 1;

        int nextX = unitX + vx * speed;
        int nextY = unitY + vy * speed;

        int clampedX = WorldConfig.clampX(nextX);
        int clampedY = WorldConfig.clampY(nextY);

        boolean hitBoundary = (clampedX != nextX) || (clampedY != nextY);
        boolean hitMapWall = collisionMask.hitUnit(clampedX, clampedY);

        if (hitBoundary || hitMapWall) {
            respawnUnit();
        } else {
            unitX = clampedX;
            unitY = clampedY;
        }

        moveObstacleX();
        moveObstacleY();

        // 기존: 장애물 충돌 리스폰
        if (isCollidingWithAnyObstacle()) {
            respawnUnit();
        }

        // ✅ 체크포인트 밟았는지 업데이트(이동/리스폰 후에 체크)
        updateCheckpointIfNeeded();

        // ✅ 엔드포인트 도착 체크
        checkEndPoint();

        return new SyncPacket(unitX, unitY, obstarcleX_x, obstarcleY_y, lastDir, elapsedMsForSync(), finished);
    }

    private boolean isCollidingWithAnyObstacle() {
        // 장애물 1: (obstarcleX_x, 400)
        boolean hitXObstacle = intersects(
                unitX, unitY, UNIT_SIZE_WIDTH, UNIT_SIZE_HEIGHT,
                obstarcleX_x, OBSTACLE_X_FIXED_Y, OBSTACLE_SIZE, OBSTACLE_SIZE
        );

        // 장애물 2: (600, obstarcleY_y)
        boolean hitYObstacle = intersects(
                unitX, unitY, UNIT_SIZE_WIDTH, UNIT_SIZE_HEIGHT,
                OBSTACLE_Y_FIXED_X, obstarcleY_y, OBSTACLE_SIZE, OBSTACLE_SIZE
        );

        return hitXObstacle || hitYObstacle;
    }

    private void moveObstacleX() {
        // 다음 위치 계산
        int next = obstarcleX_x + oX;

        // 오른쪽 끝(600) 닿으면 600에 고정 + 방향 반전(왼쪽으로)
        if (next >= 960) {
            obstarcleX_x = 960;
            oX = -1;
            return;
        }

        // 왼쪽 끝(100) 닿으면 100에 고정 + 방향 반전(오른쪽으로)
        if (next <= 390) {
            obstarcleX_x = 390;
            oX = 1;
            return;
        }

        // 범위 안이면 그냥 이동
        obstarcleX_x = next;
    }

    private void moveObstacleY() {
        int next = obstarcleY_y + oY;

        // 아래쪽 끝(OB_MAX_Y) 닿으면 아래로 못 가게 고정 + 위로 반전
        if (next >= 610) {
            obstarcleY_y = 610;
            oY = -1;
            return;
        }

        // 위쪽 끝(OB_MIN_Y) 닿으면 위로 못 가게 고정 + 아래로 반전
        if (next <= 210) {
            obstarcleY_y = 210;
            oY = 1;
            return;
        }

        obstarcleY_y = next;
    }

    private void updateCheckpointIfNeeded() {
        var cps = WorldConfig.CHECKPOINTS;
        if (cps == null || cps.length == 0) return;

        // ✅ 순서대로만 인정: 다음 체크포인트(= last+1)만 검사
        int nextIdx = lastCheckpointIdx + 1;
        if (nextIdx >= cps.length) return;

        WorldConfig.Rect cp = cps[nextIdx];
        if (intersects(unitX, unitY, UNIT_SIZE_WIDTH, UNIT_SIZE_HEIGHT,
                cp.getX(), cp.getY(), cp.getW(), cp.getH())) {
            lastCheckpointIdx = nextIdx;
            respawnX = WorldConfig.respawnX(cp);
            respawnY = WorldConfig.respawnY(cp);
        }
    }

    private void checkEndPoint() {
        WorldConfig.Rect end = WorldConfig.END_POINT;
        if (end == null) return;
        boolean onEnd = intersects(unitX, unitY, UNIT_SIZE_WIDTH, UNIT_SIZE_HEIGHT,
                end.getX(), end.getY(), end.getW(), end.getH());

        if (onEnd) {
            finished = true;
            clearTimeMs = now() - startAtMs;
            vx = 0; vy = 0;
        }
        else
            return;
    }

    private void respawnUnit() {
        unitX = WorldConfig.clampX(respawnX);
        unitY = WorldConfig.clampY(respawnY);
        vx = 0;
        vy = 0;
        lastDir = MovePacket.DOWN;
        window.printDisplay("[" + roomTitle + "]" + " 방 Unit is respawned!!!");
    }

    private boolean intersects(int ax, int ay, int aw, int ah,
                               int bx, int by, int bw, int bh) {
        return ax < bx + bw && ax + aw > bx &&
                ay < by + bh && ay + ah > by;
    }
}
