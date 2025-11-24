package server;

import shared.packet.LoginPacket;
import shared.packet.Packet;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {

    private final Socket socket;
    private final GameRoom gameRoom;
    private final ServerWindow window;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int playerId;

    public ClientHandler(Socket socket, GameRoom gameRoom, ServerWindow window) {
        this.socket = socket;
        this.gameRoom = gameRoom;
        this.window = window;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in  = new ObjectInputStream(socket.getInputStream());

            // 🔥 1) 최초 패킷 = LoginPacket
            Packet firstPacket = (Packet) in.readObject();
            if (!(firstPacket instanceof LoginPacket login)) {
                window.printDisplay("잘못된 최초 패킷 수신. 접속 종료.");
                socket.close();
                return;
            }

            String nickname = login.getNickname();

            // 🔥 2) GameRoom에 등록 (ID + 닉네임)
            this.playerId = gameRoom.addClient(this, nickname);

            window.printDisplay("플레이어 접속: ID=" + playerId + ", 닉네임=" + nickname);

            // 🔥 3) 이후부터는 일반 패킷 처리
            while (true) {
                Packet packet = (Packet) in.readObject();

                System.out.println("[SERVER] packet received: " + packet.getClass().getSimpleName());
                window.printDisplay("패킷 수신(ID=" + playerId + "): " + packet.getClass().getSimpleName());
            }

        } catch (Exception e) {
            window.printDisplay("플레이어 종료: ID=" + playerId);
        } finally {
            gameRoom.removeClient(this);
            try { socket.close(); } catch (Exception ignore) {}
        }
    }

    public int getPlayerId() {
        return playerId;
    }

    public void send(Packet packet) {
        try {
            out.writeObject(packet);
            out.flush();
        } catch (Exception ignored) {}
    }
}
