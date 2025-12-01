package server;

import shared.packet.Packet;
import shared.packet.RoomInfoPacket;

import java.util.List;
import java.util.Vector;

public class GameRoom {

    private final String roomTitle;
    private final ClientHandler host;
    private final Vector<ClientHandler> players = new Vector<>();

    private final int MAX_PLAYER = 4;

    public GameRoom(String roomTitle, ClientHandler host) {
        this.roomTitle = roomTitle;
        this.host = host;
        players.add(host);
    }

    public synchronized boolean join(ClientHandler client) {
        if (players.size() >= MAX_PLAYER) return false;

        players.add(client);
        client.setCurrentRoom(this);

        broadcastRoomInfo();
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
        List<Integer> ids = players.stream()
                .map(ClientHandler::getPlayerId)
                .toList();

        RoomInfoPacket info = new RoomInfoPacket(
                roomTitle,
                host.getPlayerId(),
                ids
        );

        broadcast(info);
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public int getPlayerCount() {
        return players.size();
    }
}
