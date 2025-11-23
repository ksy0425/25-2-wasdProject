package server;

import shared.packet.Packet;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerHandler extends Thread{
    private Socket socket;
    private GameRoom gameRoom;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    //private int playerId;

    public ServerHandler(Socket clentSocket) {
        this.socket = clentSocket;
    }

    @Override
    public void run() {
        try {
            out  = new ObjectOutputStream(socket.getOutputStream());
            in   = new ObjectInputStream(socket.getInputStream());

            //playerId = gameRoom.addPlayer(this);

            while (true) {
                Packet packet = (Packet) in.readObject();
                System.out.println("[SERVER] packet received: " + packet.getClass());
                //gameRoom.processPacket(packet, playerId);
            }

        } catch (Exception e) {
            //gameRoom.removePlayer(playerId);
        }
    }

    public void send(Packet packet) {
        try { out.writeObject(packet); } catch (Exception ignored) {}
    }
}
