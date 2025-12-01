package server;

import shared.packet.Packet;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class RoomManager {

    // 전체 클라이언트 목록
    private final Vector<ClientHandler> clients = new Vector<>();
    private final Map<Integer, String> playerNicknames = new ConcurrentHashMap<>();

    // 전체 방 목록
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
        System.out.println("삭제 닉네임 : " + playerNicknames.get(handler.getPlayerId()));
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

        GameRoom room = new GameRoom(title, host);

        rooms.put(title, room);

        serverWindow.printDisplay("[RoomManager] 방 생성: " + title);
        System.out.println("현재 인원 수 : " + room.getPlayerCount());
        return true;
    }

    public synchronized boolean joinRoom(String title, ClientHandler client) {

        GameRoom room = rooms.get(title);

        if (room == null) return false;
        // GameRoom에서 찍어야 플레이어 카운트가 정상 출력
        serverWindow.printDisplay(title + ": " + playerNicknames.get(client.getPlayerId()) + " 입장 (" + room.getPlayerCount() + "/4)");

        return room.join(client);
    }

    public synchronized void leaveRoom(ClientHandler client) {
        GameRoom room = client.getCurrentRoom();

        if (room != null) {
            room.leave(client);
            serverWindow.printDisplay("[RoomManager] 방 퇴장: ID=" + client.getPlayerId());
            if (room.isHost(client) || room.getPlayerCount() == 0) {
                removeRoom(room.getRoomTitle());
                serverWindow.printDisplay("[RoomManager] 방 삭제: Title=" + room.getRoomTitle());
            }
        }
    }

    public synchronized void removeRoom(String roomTitle) {
        GameRoom room = rooms.get(roomTitle);
        if (room == null) return;

        Vector<ClientHandler> playerCopy = room.getPlayers();

        for (ClientHandler p : playerCopy) {
            room.leave(p);
        }

        rooms.remove(roomTitle);

        serverWindow.printDisplay("[RoomManager] 방 삭제: " + roomTitle);
    }


    public GameRoom getRoom(String title) {
        return rooms.get(title);
    }
}
