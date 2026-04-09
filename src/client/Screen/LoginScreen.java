package client.Screen;

import client.network.ClientSender;
import shared.packet.LoginRequestPacket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JPanel {

    private ClientWindow window;
    private JTextField nicknameField;

    public LoginScreen(ClientWindow window) {
        this.window = window;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(createCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel createCenterPanel() { // 외부 참조
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        JLabel title = new JLabel("닉네임을 입력하세요");
        title.setFont(new Font("Serial", Font.BOLD, 24));

        nicknameField = new JTextField(15);
        nicknameField.addActionListener(onLoginEnter);

        JButton btn = new JButton("접속하기");
        btn.addActionListener(onLoginEnter);

        panel.add(title);
        panel.add(nicknameField);
        panel.add(btn);

        int pw = panel.getWidth();
        int ph = panel.getHeight();

        if (pw <= 0 || ph <= 0) {
            pw = window.getWidth();
            ph = window.getHeight();
        }

        if (pw <= 0 || ph <= 0) {
            pw = 1400;
            ph = 800;
        }

        int gap1 = 20;
        int gap2 = 30;

        Dimension titleSize = title.getPreferredSize();
        int fieldW = 200, fieldH = 40;
        Dimension btnSize = btn.getPreferredSize();

        int totalH = titleSize.height + gap1 + fieldH + gap2 + btnSize.height;
        int startY = (ph - totalH) / 2;

        int y = startY;

        title.setBounds((pw - titleSize.width) / 2, y, titleSize.width, titleSize.height);
        y += titleSize.height + gap1;

        nicknameField.setBounds((pw - fieldW) / 2, y, fieldW, fieldH);
        y += fieldH + gap2;

        btn.setBounds((pw - btnSize.width) / 2, y, btnSize.width, btnSize.height);

        return panel;
    }


    private ActionListener onLoginEnter = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            onLoginClicked();
        }
    };

    private void onLoginClicked() {
        String nickname = nicknameField.getText().trim();

        if (nickname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "닉네임을 입력하세요!");
            return;
        }

        ClientSender.send(new LoginRequestPacket(nickname));
    }
}
