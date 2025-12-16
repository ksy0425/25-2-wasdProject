package client.KeyEvent;

import client.Screen.ClientWindow;
import client.network.ClientSender;
import client.network.ConnectionManager;
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

        var handler = ConnectionManager.getHandler();
        if (handler != null && handler.getPlayers() != null) {
            handler.getPlayers().clear();
        }

        window.showScreen("main");
    }
}
