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

        boolean hitBoundary = false;

        // X 방향 경계 체크
        if (nextX < 0) {
            nextX = 0;
            hitBoundary = true;
        } else if (nextX > WORLD_WIDTH - UNIT_SIZE) {
            nextX = WORLD_WIDTH - UNIT_SIZE;
            hitBoundary = true;
        }

        // Y 방향 경계 체크
        if (nextY < 0) {
            nextY = 0;
            hitBoundary = true;
        } else if (nextY > WORLD_HEIGHT - UNIT_SIZE) {
            nextY = WORLD_HEIGHT - UNIT_SIZE;
            hitBoundary = true;
        }

        unitX = nextX;
        unitY = nextY;

        // 경계에 닿으면 멈추게 (속도 0으로)
        if (hitBoundary) {
            vx = 0;
            vy = 0;
        }
        broadcast(new SyncPacket(unitX, unitY));
    }

    public synchronized void stopGameLoop() {
        gameRunning = false;
    }
}
