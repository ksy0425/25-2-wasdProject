package server;

import shared.model.PlayerState;
import shared.packet.Packet;
import shared.packet.RoomInfoPacket;
import shared.packet.MovePacket;
import shared.packet.SyncPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GameRoom {

    private final String roomTitle;
    private final ClientHandler host;
    private final ServerWindow window;
    private final Vector<ClientHandler> players = new Vector<>();

    private final GameEngine engine;

    private final int MAX_PLAYER = 4;

    private volatile boolean gameRunning = false; // 외부 참조

    public GameRoom(String roomTitle, ClientHandler host, ServerWindow window) {
        this.roomTitle = roomTitle;
        this.host = host;
        this.window = window;
        players.add(host);
        host.setCurrentRoom(this);
        engine = new GameEngine(window, roomTitle);
    }

    public synchronized boolean join(ClientHandler client) {
        if (players.size() >= MAX_PLAYER) return false;
        players.add(client);
        client.setCurrentRoom(this);
        window.printDisplay("[" + roomTitle + "]" + ": " + client.getNickname() + " 입장 (" + players.size() + "/4)");

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

    public String getRoomTitle() { return roomTitle; }
    public int getPlayerCount() { return players.size(); }
    public int getHostId() { return host.getPlayerId(); }
    public boolean isHost(ClientHandler client) { return client.equals(host); }
    public Vector<ClientHandler> getPlayers() { return new Vector<>(players); }

    public synchronized void handleMove(MovePacket packet) {
        engine.applyMove(packet);
    }

    public synchronized void startGameLoop() {
        if (gameRunning) return;
        gameRunning = true;

        engine.scheduleStartAfter(10_000);
        engine.start();

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
        SyncPacket sync = engine.step();
        broadcast(sync);

        if (sync.getIsFinished()) {
            stopGameLoop(sync.getElapsedMs());
        }
    }

    public synchronized void stopGameLoop(long clearTime) {
        long minutes = clearTime / 60_000;
        long seconds = (clearTime % 60_000) / 1_000;
        long millis  = clearTime % 1_000;

        String formatted = String.format("%02d.%02d.%03d", minutes, seconds, millis);

        window.printDisplay(String.format("[%s] 방 게임이 종료됨. 기록:%s", roomTitle, formatted));
        gameRunning = false;
    }
}
