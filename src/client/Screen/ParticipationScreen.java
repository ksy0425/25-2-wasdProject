package client.Screen;

import client.KeyEvent.InputRoomEvent;
import client.Screen.util.BackgroundPanel;
import client.Screen.util.RoundedPanel;
import client.Screen.util.SpacerPanel;

import javax.swing.*;
import java.awt.*;

public class ParticipationScreen extends JPanel {
    private ClientWindow window;
    private JTextField t_roomTitle;

    public ParticipationScreen(ClientWindow window) {
        this.window = window;
        setLayout(new BorderLayout());
        setOpaque(false);

        BackgroundPanel bgPanel = new BackgroundPanel("/Main_Background.png");
        bgPanel.setLayout(new BorderLayout());
        add(bgPanel, BorderLayout.CENTER);

        buildGUI(bgPanel);
    }

    private void buildGUI(BackgroundPanel bgPanel) {
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        bgPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel verticalPanel = new JPanel(new BorderLayout());
        verticalPanel.setOpaque(false);
        centerPanel.add(verticalPanel);

        verticalPanel.add(createLabelPanel(), BorderLayout.NORTH);
        verticalPanel.add(createInputPanel(), BorderLayout.CENTER);
        verticalPanel.add(createSouthButtonSection(), BorderLayout.SOUTH);
    }

    private JPanel createLabelPanel() {
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setOpaque(false);

        labelPanel.add(new SpacerPanel(1, 20), BorderLayout.NORTH);

        JLabel label = new JLabel("입장하기");
        label.setFont(new Font("Dialog", Font.BOLD, 40));
        label.setForeground(Color.WHITE);
        labelPanel.add(label, BorderLayout.CENTER);

        labelPanel.add(new SpacerPanel(1, 20), BorderLayout.SOUTH);

        return labelPanel;
    }
    private JPanel createInputPanel() {
        RoundedPanel containerPanel = new RoundedPanel(30);
        containerPanel.setBackground(new Color(255, 255, 255, 220));
        containerPanel.setLayout(new BorderLayout());
        containerPanel.setPreferredSize(new Dimension(1000, 400));

        JPanel flowTitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flowTitlePanel.setOpaque(false);

        RoundedPanel titlePanel = new RoundedPanel(30);
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(new Color(180, 140, 100));
        titlePanel.setPreferredSize(new Dimension(800, 50));

        t_roomTitle = new JTextField("입장할 방 제목을 입력하세요.", 30);
        t_roomTitle.setOpaque(false);
        t_roomTitle.setFont(new Font("Dialog", Font.BOLD, 30));
        t_roomTitle.setForeground(new Color(245, 245, 220));
        t_roomTitle.addMouseListener(new InputRoomEvent(t_roomTitle));

        titlePanel.add(t_roomTitle);
        flowTitlePanel.add(titlePanel);

        JPanel flowCreatePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        flowCreatePanel.setOpaque(false);

        JButton b_create = new JButton("입장하기");
        b_create.setFont(new Font("Dialog", Font.BOLD, 40));
        b_create.setBackground(Color.GREEN);
        //b_create.addActionListener(e -> window.showScreen("host"));

        flowCreatePanel.add(b_create);

        containerPanel.add(flowTitlePanel, BorderLayout.CENTER);
        containerPanel.add(flowCreatePanel, BorderLayout.SOUTH);

        return containerPanel;
    }

    private JPanel createSouthButtonSection() {
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(new SpacerPanel(1, 20), BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.setOpaque(false);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        JButton exitButton = new JButton("   뒤로가기   ");
        exitButton.setFont(new Font("Dialog", Font.BOLD, 40));
        exitButton.setBackground(Color.GREEN);
        exitButton.addActionListener(e -> window.showScreen("main"));

        leftPanel.add(exitButton);

        buttonPanel.add(leftPanel);

        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        return southPanel;
    }

}
