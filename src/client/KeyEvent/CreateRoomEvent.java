package client.KeyEvent;

import client.Screen.ClientWindow;
import client.network.ClientSender;
import shared.packet.CreateRoomRequestPacket;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateRoomEvent implements ActionListener {
    private JTextField t_roomTitle;
    private ClientWindow window;

    public CreateRoomEvent(JTextField t_roomTitle, ClientWindow window) {
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
        ClientSender.send(new CreateRoomRequestPacket(t_roomTitle.getText()));
        window.setRoomTitle(t_roomTitle.getText());
        window.showScreen("host");
    }
}
