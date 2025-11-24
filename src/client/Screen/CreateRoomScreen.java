package client.Screen;

import client.Screen.util.BackgroundPanel;
import client.Screen.util.RoundedPanel;
import client.Screen.util.SpacerPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CreateRoomScreen extends JPanel {
    private ClientWindow window;
    private JTextField t_roomTitle;

    public CreateRoomScreen(ClientWindow window) {
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

        labelPanel.add(new SpacerPanel(1, 60), BorderLayout.NORTH);

        JLabel label = new JLabel("방 생성");
        label.setFont(new Font("Dialog", Font.BOLD, 40));
        label.setForeground(Color.WHITE);
        labelPanel.add(label, BorderLayout.CENTER);

        labelPanel.add(new SpacerPanel(1, 100), BorderLayout.SOUTH);

        return labelPanel;
    }

    private JPanel createInputPanel() {
        RoundedPanel containerPanel = new RoundedPanel(30);
        containerPanel.setBackground(new Color(255, 255, 255, 220));
        containerPanel.setLayout(new BorderLayout());
        containerPanel.setPreferredSize(new Dimension(1000, 250));

        JPanel flowTitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flowTitlePanel.setOpaque(false);
        flowTitlePanel.add(new SpacerPanel(900,60));

        RoundedPanel titlePanel = new RoundedPanel(30);
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(new Color(180, 140, 100));
        titlePanel.setPreferredSize(new Dimension(800, 70));

        t_roomTitle = new JTextField("생성할 방 제목을 입력하세요.", 22);
        t_roomTitle.setOpaque(false);
        t_roomTitle.setFont(new Font("Dialog", Font.BOLD, 40));
        t_roomTitle.setForeground(new Color(245, 245, 220));
        t_roomTitle.setBorder(new EmptyBorder(3,10,0,0));

        titlePanel.add(t_roomTitle);
        flowTitlePanel.add(titlePanel);

        JPanel flowCreatePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        flowCreatePanel.setOpaque(false);

        JButton b_create = new JButton("생성하기");
        b_create.setFont(new Font("Dialog", Font.BOLD, 40));
        b_create.setBackground(Color.GREEN);

        // 버튼 클릭 시 방 제목을 ClientWindow에 설정
        b_create.addActionListener(e -> {
            window.setRoomTitle(t_roomTitle.getText());  // 입력된 제목을 ClientWindow에 설정
            window.showScreen("host");  // HostScreen으로 화면 전환
        });

        flowCreatePanel.add(b_create);

        containerPanel.add(flowTitlePanel, BorderLayout.CENTER);
        containerPanel.add(flowCreatePanel, BorderLayout.SOUTH);

        return containerPanel;
    }

    private JPanel createSouthButtonSection() {
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(new SpacerPanel(1, 100), BorderLayout.NORTH);

        JPanel ExitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ExitPanel.setOpaque(false);
        JButton exitButton = new JButton("   나가기   ");
        exitButton.setFont(new Font("Dialog", Font.BOLD, 40));
        exitButton.setBackground(Color.GREEN);
        exitButton.addActionListener(e -> window.showScreen("main"));
        ExitPanel.add(exitButton);

        southPanel.add(ExitPanel, BorderLayout.SOUTH);

        return southPanel;
    }
}
