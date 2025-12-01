package client.KeyEvent;

import client.Screen.ClientWindow;
import client.network.ClientSender;
import server.ClientHandler;
import shared.packet.CreateRoomRequestPacket;
import shared.packet.JoinRoomRequestPacket;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JoinRoomEvent implements ActionListener {
    private JTextField t_roomTitle;
    private ClientWindow window;

    public JoinRoomEvent(JTextField t_roomTitle, ClientWindow window) {
        this.t_roomTitle = t_roomTitle;
        this.window = window;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String title = t_roomTitle.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(null, "방 이름을 입력해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            t_roomTitle.requestFocus(); // focus 다시 방 제목으로
            return;
        }
        ClientSender.send(new JoinRoomRequestPacket(t_roomTitle.getText()));
    }
}
