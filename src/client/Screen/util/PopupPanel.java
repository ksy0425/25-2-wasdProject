package client.Screen.util;

import javax.swing.*;
import java.awt.*;

public class PopupPanel extends JPanel {

    private RoundedPanel outerPanel;
    private RoundedPanel innerPanel;
    private JLabel messageLabel;
    private JButton closeButton;

    public PopupPanel(String messageText) {
        this(messageText, 40, 30); // 기본 radius
    }

    public PopupPanel(String messageText, int outerRadius, int innerRadius) {
        setLayout(new GridBagLayout());  // 화면 가운데 배치용
        setOpaque(false);

        // ===== 바깥 라운드 패널 =====
        outerPanel = new RoundedPanel(outerRadius);
        outerPanel.setBackground(new Color(180, 140, 100));
        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));
        outerPanel.setMaximumSize(new Dimension(700, 450));
        outerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ===== 내부 라운드 패널 =====
        innerPanel = new RoundedPanel(innerRadius);
        innerPanel.setBackground(new Color(255, 255, 255, 220)); // 반투명
        innerPanel.setLayout(new GridBagLayout());
        innerPanel.setMaximumSize(new Dimension(650, 280));
        innerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ===== 메시지 라벨 =====
        messageLabel = new JLabel(messageText);
        messageLabel.setFont(new Font("Dialog", Font.BOLD, 40));
        innerPanel.add(messageLabel);

        // ===== 나가기 버튼 =====
        closeButton = new JButton("   나가기   ");
        closeButton.setFont(new Font("Dialog", Font.BOLD, 40));
        closeButton.setBackground(Color.GREEN);

        closeButton.addActionListener(e -> {
            // 자신 패널 숨기기
            // this.setVisible(false);
            // 또는 remove 할 경우:
             Container parent = this.getParent();
             if (parent != null) parent.remove(this);
             parent.revalidate();
             parent.repaint();
        });

        // ===== 내부 컴포넌트 조립 =====
        outerPanel.add(Box.createVerticalStrut(30));
        outerPanel.add(innerPanel);
        outerPanel.add(Box.createVerticalStrut(30));
        outerPanel.add(closeButton);
        outerPanel.add(Box.createVerticalStrut(30));

        // 팝업을 화면 가운데 배치
        add(outerPanel);
    }

    public void setMessage(String text) {
        messageLabel.setText(text);
    }
}
