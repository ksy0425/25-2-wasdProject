package server;

import shared.packet.Packet;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class RoomManager {

    private final Vector<ClientHandler> clients = new Vector<>();
    private final Map<Integer, String> playerNicknames = new ConcurrentHashMap<>();

    private final Map<String, GameRoom> rooms = new ConcurrentHashMap<>();

    private ServerWindow serverWindow;
    private int nextPlayerId = 1;

    public RoomManager(ServerWindow serverWindow) {
        this.serverWindow = serverWindow;
    }

    public boolean check(String nickname) {
        return !playerNicknames.containsValue(nickname);
    }

    public synchronized int addClient(ClientHandler handler, String nickname) {
        clients.add(handler);

        int id = nextPlayerId++;
        playerNicknames.put(id, nickname);

        serverWindow.printDisplay("[RoomManager] 클라이언트 추가: ID=" + id + ", 닉네임=" + nickname);
        return id;
    }

    public synchronized void removeClient(ClientHandler handler) {
        leaveRoom(handler);

        clients.remove(handler);
        playerNicknames.remove(handler.getPlayerId());
        serverWindow.printDisplay("[RoomManager] 클라이언트 제거: ID=" + handler.getPlayerId());
    }

    public synchronized void broadcastLobby(Packet packet) {
        for (ClientHandler c : clients) {
            c.send(packet);
        }
    }

    public synchronized boolean createRoom(String title, ClientHandler host) {

        if (rooms.containsKey(title)) return false;

        GameRoom room = new GameRoom(title, host, serverWindow);

        rooms.put(title, room);

        serverWindow.printDisplay("[RoomManager] 방 생성: " + title);
        return true;
    }

    public synchronized boolean joinRoom(String title, ClientHandler client) {
        GameRoom room = rooms.get(title);
        if (room == null) return false;
        return room.join(client);
    }

    public synchronized void leaveRoom(ClientHandler client) {
        GameRoom room = client.getCurrentRoom();

        if (room != null) {
            room.leave(client);
            serverWindow.printDisplay("[" + room.getRoomTitle() + "]" + " 방 퇴장: ID=" + client.getPlayerId());
            if (room.isHost(client) || room.getPlayerCount() == 0) {
                removeRoom(room.getRoomTitle());
                serverWindow.printDisplay("[" + room.getRoomTitle() + "]" + " 방 삭제");
            }
        }
    }

    public synchronized void removeRoom(String roomTitle) {
        GameRoom room = rooms.get(roomTitle);
        if (room == null) return;

        room.stopGameLoop(0);

        Vector<ClientHandler> playerCopy = room.getPlayers();

        for (ClientHandler p : playerCopy) {
            room.leave(p);
        }

        rooms.remove(roomTitle);
    }


    public GameRoom getRoom(String title) {
        return rooms.get(title);
    }
}
