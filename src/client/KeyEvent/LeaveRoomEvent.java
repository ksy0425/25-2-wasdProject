package client.KeyEvent;

import client.Screen.ClientWindow;
import client.network.ClientSender;
import server.ClientHandler;
import shared.model.PlayerState;
import shared.packet.JoinRoomRequestPacket;
import shared.packet.LeaveRoomPacket;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LeaveRoomEvent implements ActionListener {
    private ClientWindow window;

    public LeaveRoomEvent(ClientWindow window) {
        this.window = window;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        ClientSender.send(new LeaveRoomPacket());
        window.showScreen("main");
    }
}
