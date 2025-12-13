package server;

import shared.model.PlayerState;
import shared.packet.Packet;
import shared.packet.RoomInfoPacket;
import shared.packet.MovePacket;
import shared.packet.SyncPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static client.game.player.Unit.UNIT_SIZE;

public class GameRoom {

    private final String roomTitle;
    private final ClientHandler host;
    private final Vector<ClientHandler> players = new Vector<>();

    private final int MAX_PLAYER = 4;

    private static final int WORLD_WIDTH = 1400;
    private static final int WORLD_HEIGHT = 800;

    // ✅ 유닛 스폰(리스폰) 위치
    private static final int SPAWN_X = 200;
    private static final int SPAWN_Y = 200;

    // ✅ 장애물 고정 좌표(클라이언트 Obstarcle 생성 좌표와 반드시 동일해야 함)
    private static final int OBSTACLE_X_FIXED_Y = 400; // (obstarcleX_x, 400)
    private static final int OBSTACLE_Y_FIXED_X = 600; // (600, obstarcleY_y)

    private int unitX = SPAWN_X;
    private int unitY = SPAWN_Y;
    private int vx = 0;
    private int vy = 0;

    private int obstarcleX_x = 100;
    private int obstarcleY_y = 100;

    private int oX = 0;
    private int oY = 0;

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
        // 어떤 플레이어의 입력이든, 가장 마지막에 온 방향이 현재 방향이 된다.
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
        int speed = 1;
        int nextX = unitX + vx * speed;
        int nextY = unitY + vy * speed;

        int clampedX = collisionX(nextX);
        int clampedY = collisionY(nextY);

        boolean hitBoundary = (clampedX != nextX) || (clampedY != nextY);

        // =========================================================
        // ✅ [나중에 추가할 곳] "맵 벽(타일/장애물/벽체) 충돌 검사" 위치
        //    - 검사 대상 좌표는 보통 (clampedX, clampedY) 또는 (nextX, nextY)
        //    - 예: boolean hitMapWall = hitMapWall(clampedX, clampedY);
        // =========================================================
        // boolean hitMapWall = hitMapWall(clampedX, clampedY);

        // ✅ 벽(월드 경계) 또는 맵 벽 충돌이면 리스폰 처리
        // if (hitBoundary || hitMapWall) {
        if (hitBoundary) { // 지금은 경계만 적용, 나중에 hitMapWall OR 조건으로 묶기
            respawnUnit();
        } else {
            unitX = clampedX;
            unitY = clampedY;
        }

        // 장애물 이동
        moveObstacleX();
        moveObstacleY();

        // 기존: 장애물 충돌 리스폰
        if (isCollidingWithAnyObstacle()) {
            respawnUnit();
        }

        broadcast(new SyncPacket(unitX, unitY, obstarcleX_x, obstarcleY_y));
    }

    private void respawnUnit() {
        unitX = SPAWN_X;
        unitY = SPAWN_Y;
        vx = 0;
        vy = 0;
    }

    private boolean isCollidingWithAnyObstacle() {
        // 장애물 1: (obstarcleX_x, 400)
        boolean hitXObstacle = intersects(
                unitX, unitY, UNIT_SIZE, UNIT_SIZE,
                obstarcleX_x, OBSTACLE_X_FIXED_Y, UNIT_SIZE, UNIT_SIZE
        );

        // 장애물 2: (600, obstarcleY_y)
        boolean hitYObstacle = intersects(
                unitX, unitY, UNIT_SIZE, UNIT_SIZE,
                OBSTACLE_Y_FIXED_X, obstarcleY_y, UNIT_SIZE, UNIT_SIZE
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
        if (x > WORLD_WIDTH - UNIT_SIZE) return WORLD_WIDTH - UNIT_SIZE;
        return x;
    }

    private int collisionY(int y) {
        if (y < 0) return 0;
        if (y > WORLD_HEIGHT - UNIT_SIZE) return WORLD_HEIGHT - UNIT_SIZE;
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

    public synchronized void stopGameLoop() {
        gameRunning = false;
    }
}
