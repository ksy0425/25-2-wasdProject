package server;

import shared.packet.LoginRequestPacket;
import shared.packet.Packet;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {

    private final Socket socket;
    private final RoomManager roomManager;
    private final ServerWindow window;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String nickname;
    private int playerId;
    private GameRoom currentRoom;

    public ClientHandler(Socket socket, RoomManager roomManager, ServerWindow window) {
        this.socket = socket;
        this.roomManager = roomManager;
        this.window = window;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in  = new ObjectInputStream(socket.getInputStream());

            PacketHandler handler = new PacketHandler(this, roomManager, window);

            Packet first = (Packet) in.readObject();
            if (!(first instanceof LoginRequestPacket)) {
                window.printDisplay("잘못된 최초 패킷 수신. 접속 종료.");
                socket.close();
                return;
            }

            handler.handle(first);

            while (true) {
                Packet packet = (Packet) in.readObject();
                handler.handle(packet);
            }

        } catch (Exception e) {
            window.printDisplay("플레이어 종료: ID=" + playerId);

        } finally {
            roomManager.removeClient(this);
            try { socket.close(); } catch (Exception ignore) {}
        }
    }

    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public int getPlayerId() { return playerId; }
    public String getNickname() { return nickname; }
    public synchronized GameRoom getCurrentRoom() { return currentRoom; }
    public synchronized void setCurrentRoom(GameRoom room) { this.currentRoom = room; }

    public void send(Packet packet) {
        try {
            out.writeObject(packet);
            out.flush();
        } catch (Exception ignored) {}
    }
}

