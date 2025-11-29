package client.network;

import shared.packet.Packet;

public class ClientSender {

    public static synchronized void send(Packet packet) {
        try {
            ConnectionManager.out.writeObject(packet);
            ConnectionManager.out.flush();
        } catch (Exception e) {
            System.out.println("[ClientSender] 전송 실패: " + e.getMessage());
        }
    }
}
