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
    private int unitX = 200;
    private int unitY = 200;
    private int vx = 0;
    private int vy = 0;
    private int obstarcleX_x = 100;
    //private int obstarcleX_y = 400;
    //private int obstarcleY_x = 600;
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

//        broadcastRoomInfo();
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
            case MovePacket.STOP -> {
                vx = 0;
                vy = 0;
            }
            case MovePacket.LEFT -> {
                vx = -1;
                vy = 0;
            }
            case MovePacket.RIGHT -> {
                vx = 1;
                vy = 0;
            }
            case MovePacket.UP -> {
                vx = 0;
                vy = -1;
            }
            case MovePacket.DOWN -> {
                vx = 0;
                vy = 1;
            }
        }
        // 어떤 플레이어의 입력이든, 가장 마지막에 온 방향이 현재 방향이 된다.
    }

    public synchronized void startGameLoop() {
        if (gameRunning) return;   // 중복 시작 방지
        gameRunning = true;
        oX = 1;
        oY = 1;
        Thread loop = new Thread(() -> {
            while (gameRunning) {
                stepGame();
                try {
                    Thread.sleep(16); // 약 60FPS
                } catch (InterruptedException ignored) {}
            }
        }, "GameLoop-" + roomTitle);
        loop.setDaemon(true); // 모든 일반(non-daemon) 스레드가 종료되면, 데몬 스레드는 강제로 같이 종료
        loop.start();
    }

    private synchronized void stepGame() {
        int speed = 1;
        int nextX = unitX + vx * speed;
        int nextY = unitY + vy * speed;

        // ✅ 경계 체크(보정) 메서드 사용
        int clampedX = collisionX(nextX);
        int clampedY = collisionY(nextY);

        boolean hitBoundary = (clampedX != nextX) || (clampedY != nextY);

        unitX = clampedX;
        unitY = clampedY;

        moveObstacleX();
        moveObstacleY();

        if (hitBoundary) {
            vx = 0;
            vy = 0;
        }

        broadcast(new SyncPacket(unitX, unitY, obstarcleX_x, obstarcleY_y));
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
        // 다음 위치 계산
        int next = obstarcleX_x + oX;

        // 오른쪽 끝(600) 닿으면 600에 고정 + 방향 반전(왼쪽으로)
        if (next >= 600) {
            obstarcleX_x = 600;
            oX = -1;
            return;
        }

        // 왼쪽 끝(100) 닿으면 100에 고정 + 방향 반전(오른쪽으로)
        if (next <= 100) {
            obstarcleX_x = 100;
            oX = 1;
            return;
        }

        // 범위 안이면 그냥 이동
        obstarcleX_x = next;
    }

    private void moveObstacleY() {
        int next = obstarcleY_y + oY;

        // 아래쪽 끝(OB_MAX_Y) 닿으면 아래로 못 가게 고정 + 위로 반전
        if (next >= 600) {
            obstarcleY_y = 600;
            oY = -1;
            return;
        }

        // 위쪽 끝(OB_MIN_Y) 닿으면 위로 못 가게 고정 + 아래로 반전
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
