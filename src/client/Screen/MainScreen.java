package client.Screen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainScreen extends JFrame {
    private JButton b_host, b_participation, b_exit;

    public MainScreen() {
        super("WASD: 부기의 모험");

        setContentPane(new BackgroundPanel("/Main_Background.png")); // 🔥 배경 설정

        buildGUI();

        setSize(1400, 800);
        setLocation(100, 10);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void buildGUI() {
        setLayout(new BorderLayout());

        // 패널들은 배경이 보이도록 반드시 투명하게!
        JPanel titlePanel = createTitlePanel();
        titlePanel.setOpaque(false);

        JPanel controlPanel = createControlPanel();
        controlPanel.setOpaque(false);

        add(titlePanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.CENTER);
    }

    // 🔹 타이틀 이미지
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBorder(new EmptyBorder(100, 0, 0, 0));
        titlePanel.setOpaque(false);

        ImageIcon wasdIcon = new ImageIcon(getClass().getResource("/WASD.png"));
        ImageIcon adventureIcon = new ImageIcon(getClass().getResource("/AdventureOfBugi.png"));

        Image wasdImg = wasdIcon.getImage().getScaledInstance(350, -1, Image.SCALE_SMOOTH);
        Image adventureImg = adventureIcon.getImage().getScaledInstance(300, -1, Image.SCALE_SMOOTH);

        JLabel title1 = new JLabel(new ImageIcon(wasdImg));
        JLabel title2 = new JLabel(new ImageIcon(adventureImg));

        title1.setAlignmentX(Component.CENTER_ALIGNMENT);
        title2.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(title1);
        titlePanel.add(title2);

        return titlePanel;
    }

    private JPanel createControlPanel() {
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flowPanel.setOpaque(false);
        flowPanel.setBorder(new EmptyBorder(50, 0, 0, 0));

        JPanel gridPanel = new JPanel(new GridLayout(0, 1));
        gridPanel.setOpaque(false);

        gridPanel.add(createButtonCell("호스트", 10, 0, 0, 0));
        gridPanel.add(createButtonCell("참가하기", 10, 0, 0, 0));
        gridPanel.add(createButtonCell("종료", 10, 0, 0, 0));

        flowPanel.add(gridPanel);

        return flowPanel;
    }

    private JPanel createButtonCell(String text, int top, int left, int bottom, int right) {
        JPanel panel = new JPanel();
        panel.setOpaque(false); // 배경 보이기
        panel.setBorder(new EmptyBorder(top, left, bottom, right));

        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(400, 50));

        switch (text) {
            case "호스트" -> b_host = button;
            case "참가하기" -> b_participation = button;
            case "종료" -> b_exit = button;
        }

        panel.add(button);
        return panel;
    }
}
