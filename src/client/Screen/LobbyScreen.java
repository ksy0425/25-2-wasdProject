package client.Screen;

import client.KeyEvent.LeaveRoomEvent;
import client.Screen.util.BackgroundPanel;
import client.Screen.util.RoomPanel;
import client.Screen.util.RoundedPanel;
import client.Screen.util.SpacerPanel;
import client.network.ConnectionManager;
import shared.model.PlayerState;

import javax.swing.*;
import java.awt.*;

public class LobbyScreen extends JPanel {

    private ClientWindow window;
    private JButton b_back;
    private String title;
    private int hostId;
    private RoomPanel roomPanel;

    public LobbyScreen(ClientWindow window) {
        this.window = window;
        this.title = window.getRoomTitle();

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

        verticalPanel.add(createTitleSection(), BorderLayout.NORTH);
        verticalPanel.add(createRoomCard(), BorderLayout.CENTER);
        verticalPanel.add(createSouthButtonSection(), BorderLayout.SOUTH);
    }

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

    private JPanel createRoomCard() {
        RoundedPanel roomCard = new RoundedPanel(30);
        roomCard.setBackground(new Color(255, 255, 255, 220));
        roomCard.setLayout(new BorderLayout());
        roomCard.setPreferredSize(new Dimension(1000, 500));

        roomCard.add(createRoomPanel());

        return roomCard;
    }

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
        //exitButton.addActionListener(e -> window.showScreen("main"));
        exitButton.addActionListener(new LeaveRoomEvent(window));
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

    private JPanel createRoomPanel() {
        roomPanel = new RoomPanel();
        roomPanel.setOpaque(false);

        refreshParticipants();

        return roomPanel;
    }

    public void refreshParticipants() {
        if (roomPanel == null) {
            return;
        }

        var handler = ConnectionManager.getHandler();
        if (handler == null) return;

        roomPanel.clearParticipants();

        for (PlayerState ps : handler.getPlayers().values()) {
            roomPanel.addParticipant(ps.getNickname());
        }
    }
    public boolean isHost() {
        return true;
    }
}
