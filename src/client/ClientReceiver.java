package client;

import client.network.ConnectionManager;
//import client.network.GameState;
import shared.packet.Packet;
import shared.packet.SyncPacket;

public class ClientReceiver extends Thread {

    @Override
    public void run() {
        try {
            while (true) {
                Packet p = (Packet) ConnectionManager.in.readObject();

            }
        } catch (Exception e) {
            System.out.println("Disconnected.");
        }
    }
}