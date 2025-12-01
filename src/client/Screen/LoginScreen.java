package client.Screen;

import client.network.ClientSender;
import client.network.ConnectionManager;
import shared.packet.LoginRequestPacket;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JPanel {

    private ClientWindow window;
    private JTextField nicknameField;

    public LoginScreen(ClientWindow window) {
        this.window = window;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(createCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel title = new JLabel("닉네임을 입력하세요");
        title.setFont(new Font("Serial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        nicknameField = new JTextField(15);
        nicknameField.setMaximumSize(new Dimension(200, 40));
        nicknameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btn = new JButton("접속하기");
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> onLoginClicked());

        panel.add(Box.createVerticalGlue());
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(nicknameField);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(btn);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void onLoginClicked() {
        String nickname = nicknameField.getText().trim();

        if (nickname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "닉네임을 입력하세요!");
            return;
        }

        ClientSender.send(new LoginRequestPacket(nickname));
    }
}
