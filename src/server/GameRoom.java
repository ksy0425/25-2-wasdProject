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

    // ✅ 게임 엔진(상태/이동/충돌/리스폰/Sync 생성 담당)
    private final GameEngine engine;

    private final int MAX_PLAYER = 4;

    private volatile boolean gameRunning = false;

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

    // ✅ MovePacket 처리: GameRoom은 “입력 전달”만
    public synchronized void handleMove(MovePacket packet) {
        engine.applyMove(packet);
    }

    public synchronized void startGameLoop() {
        if (gameRunning) return;
        gameRunning = true;

        // ✅ 기존 동작 유지: 장애물 이동 시작(oX=1,oY=1)
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

    // ✅ GameRoom은 “한 틱 진행 + SyncPacket 브로드캐스트”만
    private synchronized void stepGame() {
        SyncPacket sync = engine.step();
        broadcast(sync);

        if (sync.getIsFinished()) {
            stopGameLoop();
        }
    }

    public synchronized void stopGameLoop() {
        gameRunning = false;
    }
}
