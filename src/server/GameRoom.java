package server;

import shared.model.PlayerState;
import shared.packet.Packet;
import shared.packet.RoomInfoPacket;
import shared.packet.MovePacket;
import shared.packet.SyncPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import static client.game.player.Unit.*;

public class GameRoom {

    private final String roomTitle;
    private final ClientHandler host;
    private final Vector<ClientHandler> players = new Vector<>();

    private final int MAX_PLAYER = 4;

    private static final int WORLD_WIDTH = 1400;
    private static final int WORLD_HEIGHT = 800;

    // ✅ 충돌 마스크 (흰색=안전, 검정/어두운색=즉사)
    private static final BufferedImage COLLISION_MASK = loadCollisionMask();

    // "흰색만 통과"를 엄격하게: 거의 흰색(>=250)만 안전 처리
    private static final int SAFE_LUMINANCE = 250;

    // ✅ 유닛 스폰(리스폰) 위치
    // ✅ 스폰 기준점 2개 (유닛 "중심"이 여기 중간에 오게 만들 것)
    private static final double SPAWN_P1_X = 85.0;
    private static final double SPAWN_P1_Y = 674.0;
    private static final double SPAWN_P2_X = 210.0;
    private static final double SPAWN_P2_Y = 782.0;

    // ✅ 두 점의 중간점 - (유닛 너비/높이의 절반) => 유닛 좌상단 좌표
    private static final int SPAWN_X = (int) Math.round(((SPAWN_P1_X + SPAWN_P2_X) / 2.0) - (UNIT_SIZE_WIDTH / 2.0));
    private static final int SPAWN_Y = (int) Math.round(((SPAWN_P1_Y + SPAWN_P2_Y) / 2.0) - (UNIT_SIZE_HEIGHT / 2.0));

    // ✅ 체크포인트/엔드 사각형 (좌상단(x1,y1) ~ 우하단(x2,y2))
    private static final int CP1_X1 = 369, CP1_Y1 = 27,  CP1_X2 = 494, CP1_Y2 = 139;
    private static final int CP2_X1 = 1041, CP2_Y1 = 686, CP2_X2 = 1166, CP2_Y2 = 799;

    // end는 "아래 변(y=195) 라인"을 밟으면 종료라고 했으니, 일단 사각형으로 처리
    private static final int END_X1 = 1162, END_Y1 = 60,  END_X2 = 1292, END_Y2 = 173;

    // ✅ 현재 리스폰(스폰) 위치: 시작은 start 중앙으로
    private int respawnX = SPAWN_X;
    private int respawnY = SPAWN_Y;

    // 밑에는 clear하기 위한 테스트용
//    private int respawnX = 1211;
//    private int respawnY = 280;

    // ✅ 중복 처리 방지(체크포인트 여러 번 밟아도 스폰만 갱신)
    private boolean cp1Activated = false;
    private boolean cp2Activated = false;

    // ✅ 게임 종료 플래그
    private boolean finished = false;

    // ✅ 장애물 고정 좌표(클라이언트 Obstarcle 생성 좌표와 반드시 동일해야 함)
    private static final int OBSTACLE_X_FIXED_Y = 400; // (obstarcleX_x, 400)
    private static final int OBSTACLE_Y_FIXED_X = 600; // (600, obstarcleY_y)

    private int unitX = SPAWN_X;
    private int unitY = SPAWN_Y;
    private int vx = 0;
    private int vy = 0;
    private int lastDir = 0;

    private int obstarcleX_x = 100;
    private int obstarcleY_y = 100;

    private int oX = 0;
    private int oY = 0;

    //타임 어택 관련
    private long startedAtMs = -1;
    private long clearTimeMs = -1;

    private volatile boolean gameRunning = false;

    public GameRoom(String roomTitle, ClientHandler host) {
        this.roomTitle = roomTitle;
        this.host = host;
        players.add(host);
        host.setCurrentRoom(this);
    }

    public synchronized boolean join(ClientHandler client) {
        if (players.size() >= MAX_PLAYER) return false;

        players.add(client);
        client.setCurrentRoom(this);
        return true;
    }

    public synchronized void leave(ClientHandler client) {
        if (!players.contains(client)) return;
        players.remove(client);
        client.setCurrentRoom(null);
        broadcastRoomInfo();
    }

    public synchronized void broadcast(Packet packet) {
        for (ClientHandler p : players) {
            p.send(packet);
        }
    }

    public synchronized void broadcastRoomInfo() {
        List<PlayerState> states = new ArrayList<>();

        for (ClientHandler c : players) {
            PlayerState ps = new PlayerState(c.getNickname(), c.getPlayerId());
            states.add(ps);
        }

        RoomInfoPacket info = new RoomInfoPacket(
                roomTitle,
                host.getPlayerId(),
                states
        );

        broadcast(info);
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public int getHostId() { return host.getPlayerId(); }
    public boolean isHost(ClientHandler client) { return client.equals(host); }
    public Vector<ClientHandler> getPlayers() { return new Vector<>(players); }

    public synchronized void handleMove(MovePacket packet) {
        int dir = packet.getDirection();
        switch (dir) {
            case MovePacket.STOP -> { vx = 0; vy = 0; }
            case MovePacket.LEFT -> { vx = -1; vy = 0; }
            case MovePacket.RIGHT -> { vx = 1; vy = 0; }
            case MovePacket.UP -> { vx = 0; vy = -1; }
            case MovePacket.DOWN -> { vx = 0; vy = 1; }
        }
        if (dir != MovePacket.STOP) {
            lastDir = dir;
        }
    }

    public synchronized void startGameLoop() {
        if (gameRunning) return;
        gameRunning = true;
        oX = 1;
        oY = 1;

        Thread loop = new Thread(() -> {
            while (gameRunning) {
                stepGame();
                try {
                    Thread.sleep(16);
                } catch (InterruptedException ignored) {}
            }
        }, "GameLoop-" + roomTitle);

        loop.setDaemon(true);
        loop.start();
    }

    private synchronized void stepGame() {
        // ✅ 시작 전: 움직임/충돌/리스폰 X, 그냥 카운트다운만 Sync로 뿌림
        if (elapsedMs() < 0) {
            broadcast(new SyncPacket(unitX, unitY, obstarcleX_x, obstarcleY_y, lastDir, elapsedMs(), finished));
            return;
        }

        int speed = 2;
        int nextX = unitX + vx * speed;
        int nextY = unitY + vy * speed;

        int clampedX = collisionX(nextX);
        int clampedY = collisionY(nextY);

        boolean hitBoundary = (clampedX != nextX) || (clampedY != nextY);

        // ✅ 마스크 검정(어두운)과 충돌 검사
        boolean hitMapWall = hitBlackMask(clampedX, clampedY);

        // ✅ 경계 OR 마스크 충돌이면 리스폰
        if (hitBoundary || hitMapWall) {
            respawnUnit();
        } else {
            unitX = clampedX;
            unitY = clampedY;
        }

        // 장애물 이동 & 충돌
//        moveObstacleX();
//        moveObstacleY();

//        if (isCollidingWithAnyObstacle()) {
//            respawnUnit();
//        }

        // unitX/unitY가 확정된 다음
        updateCheckpointAndEnd();

        // 끝났으면 마지막 상태 한 번 뿌리고 return (선택)
        if (finished) {
            clearTimeMs = now() - startedAtMs;
            broadcast(new SyncPacket(unitX, unitY, obstarcleX_x, obstarcleY_y, lastDir, clearTimeMs, finished));
            stopGameLoop();
            return;
        }

        broadcast(new SyncPacket(unitX, unitY, obstarcleX_x, obstarcleY_y, lastDir, elapsedMs(), finished));
    }

    private void respawnUnit() {
        unitX = collisionX(respawnX);
        unitY = collisionY(respawnY);
        vx = 0;
        vy = 0;
        lastDir = 0;
    }

    private boolean isCollidingWithAnyObstacle() {
        // 장애물 1: (obstarcleX_x, 400)
        boolean hitXObstacle = intersects(
                unitX, unitY, UNIT_SIZE_WIDTH, UNIT_SIZE_HEIGHT,
                obstarcleX_x, OBSTACLE_X_FIXED_Y, UNIT_SIZE_WIDTH, UNIT_SIZE_HEIGHT
        );

        // 장애물 2: (600, obstarcleY_y)
        boolean hitYObstacle = intersects(
                unitX, unitY, UNIT_SIZE_WIDTH, UNIT_SIZE_HEIGHT,
                OBSTACLE_Y_FIXED_X, obstarcleY_y, UNIT_SIZE_WIDTH, UNIT_SIZE_HEIGHT
        );

        return hitXObstacle || hitYObstacle;
    }

    private boolean intersects(int ax, int ay, int aw, int ah,
                               int bx, int by, int bw, int bh) {
        return ax < bx + bw && ax + aw > bx &&
                ay < by + bh && ay + ah > by;
    }

    private int collisionX(int x) {
        if (x < 0) return 0;
        if (x > WORLD_WIDTH - UNIT_SIZE_WIDTH) return WORLD_WIDTH - UNIT_SIZE_WIDTH;
        return x;
    }

    private int collisionY(int y) {
        if (y < 0) return 0;
        if (y > WORLD_HEIGHT - UNIT_SIZE_HEIGHT) return WORLD_HEIGHT - UNIT_SIZE_HEIGHT;
        return y;
    }

    private void moveObstacleX() {
        int next = obstarcleX_x + oX;

        if (next >= 600) {
            obstarcleX_x = 600;
            oX = -1;
            return;
        }

        if (next <= 100) {
            obstarcleX_x = 100;
            oX = 1;
            return;
        }

        obstarcleX_x = next;
    }

    private void moveObstacleY() {
        int next = obstarcleY_y + oY;

        if (next >= 600) {
            obstarcleY_y = 600;
            oY = -1;
            return;
        }

        if (next <= 100) {
            obstarcleY_y = 100;
            oY = 1;
            return;
        }

        obstarcleY_y = next;
    }

    private boolean hitRect(int rx1, int ry1, int rx2, int ry2) {
        int w = rx2 - rx1;
        int h = ry2 - ry1;
        return intersects(unitX, unitY, UNIT_SIZE_WIDTH, UNIT_SIZE_HEIGHT, rx1, ry1, w, h);
    }

    private int rectCenterSpawnX(int x1, int x2) {
        double cx = (x1 + x2) / 2.0;
        return (int) Math.round(cx - (UNIT_SIZE_WIDTH / 2.0));
    }

    private int rectCenterSpawnY(int y1, int y2) {
        double cy = (y1 + y2) / 2.0;
        return (int) Math.round(cy - (UNIT_SIZE_HEIGHT / 2.0));
    }

    private void updateCheckpointAndEnd() {
        // 이미 끝났으면 더 처리 안 함
        if (finished) return;

        // ✅ 체크포인트1
        if (!cp1Activated && hitRect(CP1_X1, CP1_Y1, CP1_X2, CP1_Y2)) {
            cp1Activated = true;
            respawnX = collisionX(rectCenterSpawnX(CP1_X1, CP1_X2));
            respawnY = collisionY(rectCenterSpawnY(CP1_Y1, CP1_Y2));
        }

        // ✅ 체크포인트2
        if (!cp2Activated && hitRect(CP2_X1, CP2_Y1, CP2_X2, CP2_Y2)) {
            cp2Activated = true;
            respawnX = collisionX(rectCenterSpawnX(CP2_X1, CP2_X2));
            respawnY = collisionY(rectCenterSpawnY(CP2_Y1, CP2_Y2));
        }

        // ✅ END: "아래 변(y=195) 라인" 느낌을 살리려면,
        //    유닛이 END 사각형에 닿았는지(간단), 또는 y=END_Y2 근처 라인 접촉으로 더 엄격히 할 수 있음.
        if (hitRect(END_X1, END_Y1, END_X2, END_Y2)) {
            finished = true;
            gameRunning = false; // 루프 종료
            vx = 0; vy = 0;
        }
    }


    private static BufferedImage loadCollisionMask() {
        String[] candidates = {"/CollusionMask.png", "/collusionMask.png", "/CollisionMask.png"};
        for (String path : candidates) {
            try (InputStream is = GameRoom.class.getResourceAsStream(path)) {
                if (is == null) continue;
                return ImageIO.read(is);
            } catch (Exception ignored) {}
        }
        throw new RuntimeException("충돌 마스크 로딩 실패: resources에 CollusionMask.png가 없음");
    }

    private static boolean isDangerPixel(int argb) {
        int a = (argb >>> 24) & 0xFF;
        if (a == 0) return false; // 투명은 안전 처리(마스크에 투명 없으면 영향 없음)

        int r = (argb >>> 16) & 0xFF;
        int g = (argb >>> 8) & 0xFF;
        int b = (argb) & 0xFF;
        int lum = (r + g + b) / 3;

        // SAFE_LUMINANCE 미만이면 검정/어두운 영역으로 보고 즉사
        return lum < SAFE_LUMINANCE;
    }

    /**
     * 유닛 좌상단 (ux,uy) 기준으로 UNIT_SIZE_WIDTH x UNIT_SIZE_HEIGHT 영역이
     * 마스크의 검정(어두운) 영역과 겹치면 true.
     */
    private boolean hitBlackMask(int ux, int uy) {
        int mw = COLLISION_MASK.getWidth();
        int mh = COLLISION_MASK.getHeight();

        // 바깥은 검정으로 취급(즉사)
        if (ux < 0 || uy < 0 || ux + UNIT_SIZE_WIDTH > mw || uy + UNIT_SIZE_HEIGHT > mh) {
            return true;
        }

        // 유닛 크기(30x40)라서 전 픽셀 검사해도 부담 거의 없음(정확도 최우선)
        for (int y = uy; y < uy + UNIT_SIZE_HEIGHT; y++) {
            for (int x = ux; x < ux + UNIT_SIZE_WIDTH; x++) {
                if (isDangerPixel(COLLISION_MASK.getRGB(x, y))) {
                    return true;
                }
            }
        }
        return false;
    }

    public long now() { return System.currentTimeMillis(); }

    public long elapsedMs() {
        if (startedAtMs < 0) return 0;
        return now() - startedAtMs;
    }

    public void startTimerIfNeeded() {
        if (startedAtMs < 0) startedAtMs = now() + 10_000;
    }

    public synchronized void stopGameLoop() {
        gameRunning = false;
    }
}
