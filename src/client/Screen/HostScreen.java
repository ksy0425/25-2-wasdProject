package client.Screen;

import client.Screen.util.BackgroundPanel;
import client.Screen.util.RoomPanel;
import client.Screen.util.RoundedPanel;
import client.Screen.util.SpacerPanel;

import javax.swing.*;
import java.awt.*;

public class HostScreen extends JPanel {

    private ClientWindow window;
    private JButton b_back;
    private String title;

    public HostScreen(ClientWindow window) {
        this.window = window;
        this.title = window.getRoomTitle();
        System.out.println("title : " + title);

        setLayout(new BorderLayout());
        setOpaque(false);

        BackgroundPanel bgPanel = new BackgroundPanel("/Main_Background.png");
        bgPanel.setLayout(new BorderLayout());
        add(bgPanel, BorderLayout.CENTER);

        buildGUI(bgPanel);
    }

    private void buildGUI(JPanel bgPanel) {

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        bgPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel verticalPanel = new JPanel();
        verticalPanel.setOpaque(false);
        verticalPanel.setLayout(new BorderLayout());
        centerPanel.add(verticalPanel);

        // 각각의 메서드에서 패널을 리턴받아 add만 처리
        verticalPanel.add(createTitleSection(), BorderLayout.NORTH);
        verticalPanel.add(createRoomCard(), BorderLayout.CENTER);
        verticalPanel.add(createSouthButtonSection(), BorderLayout.SOUTH);
    }

    // 상단 타이틀 생성
    private JPanel createTitleSection() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        titlePanel.add(new SpacerPanel(1, 20), BorderLayout.NORTH);

        JLabel l_title = new JLabel(title);
        l_title.setFont(new Font("Dialog", Font.BOLD, 40));
        l_title.setForeground(Color.WHITE);
        titlePanel.add(l_title, BorderLayout.CENTER);

        titlePanel.add(new SpacerPanel(1, 20), BorderLayout.SOUTH);

        return titlePanel;
    }

    // 둥근 흰색 박스 생성
    private JPanel createRoomCard() {
        RoundedPanel roomCard = new RoundedPanel(30);
        roomCard.setBackground(new Color(255, 255, 255, 220));
        roomCard.setLayout(new BorderLayout());
        roomCard.setPreferredSize(new Dimension(1000, 500));

        roomCard.add(createRoomPanel());

        return roomCard;
    }

    // 아래쪽 버튼 영역 생성
    private JPanel createSouthButtonSection() {
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(new SpacerPanel(1, 20), BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.setOpaque(false);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        JButton exitButton = new JButton("   나가기   ");
        exitButton.setFont(new Font("Dialog", Font.BOLD, 40));
        exitButton.setBackground(Color.GREEN);
        exitButton.addActionListener(e -> window.showScreen("main"));
        leftPanel.add(exitButton);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        JButton startButton = new JButton("시작하기");
        startButton.setFont(new Font("Dialog", Font.BOLD, 40));
        startButton.setBackground(Color.GREEN);
        rightPanel.add(startButton);

        buttonPanel.add(leftPanel);
        buttonPanel.add(rightPanel);

        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        return southPanel;
    }

    // 참가자 목록 패널 생성
    private JPanel createRoomPanel() {
        RoomPanel roomPanel = new RoomPanel();
        roomPanel.setOpaque(false);
        // roomCard의 CENTER에 roomPanel 추가
        // RoomPanel을 생성하고 직접 add 하면 됨
        roomPanel.addParticipant("Player1");
        roomPanel.addParticipant("Player2");

        return roomPanel;
    }
}
