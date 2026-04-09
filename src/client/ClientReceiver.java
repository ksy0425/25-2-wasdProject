package client;

import client.network.ConnectionManager;
import shared.packet.Packet;

public class ClientReceiver extends Thread {

    private final ClientPacketHandler handler;

    public ClientReceiver(ClientPacketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Packet p = (Packet) ConnectionManager.in.readObject();
                if (p == null) {
                    System.out.println("[CLIENT] null 패킷 수신 - 수신 스레드 종료");
                    break;
                }

                handler.handle(p);
            }
        } catch (Exception e) {
            System.out.println("[CLIENT] Disconnected from server: " + e.getMessage());
            handler.onDisconnected();
        }
    }
}
