package server;

import shared.packet.Packet;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class GameRoom {

    private final Vector<ClientHandler> clients = new Vector<>();
    private final Map<Integer, String> playerNicknames = new ConcurrentHashMap<>();

    private int nextPlayerId = 1;

    public synchronized int addClient(ClientHandler handler, String nickname) {
        clients.add(handler);

        int id = nextPlayerId++;
        playerNicknames.put(id, nickname);

        System.out.println("[GameRoom] 클라이언트 추가. ID=" + id + ", 닉네임=" + nickname);
        return id;
    }

    public synchronized void removeClient(ClientHandler handler) {
        clients.remove(handler);

        // playerId를 알 수 있어야 하는데, handler 안에 playerId 저장되어 있음
        int id = handler.getPlayerId();
        playerNicknames.remove(id);

        System.out.println("[GameRoom] 클라이언트 제거. ID=" + id);
    }

    public String getNickname(int playerId) {
        return playerNicknames.get(playerId);
    }

    public synchronized void broadcast(Packet packet) {
        for (ClientHandler client : clients) {
            client.send(packet);
        }
    }
}
