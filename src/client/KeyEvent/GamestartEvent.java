package client.KeyEvent;

import client.network.ClientSender;
import client.network.ConnectionManager;
import server.ClientHandler;
import shared.packet.GameStartRequestPacket;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GamestartEvent implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        ClientSender.send(new GameStartRequestPacket());
    }
}
