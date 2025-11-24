package client.Screen;

import client.Screen.util.BackgroundPanel;
import client.Screen.util.ImagePanel;
import client.KeyEvent.StartEvent;
import client.KeyEvent.ExitEvent;

import javax.swing.*;
import java.awt.*;

public class MainScreen extends JPanel {
    private JButton b_host, b_participation, b_exit;
    private ClientWindow window;

    public MainScreen(ClientWindow window) {
        this.window = window;

        setLayout(new BorderLayout());
        setOpaque(false);

        BackgroundPanel bgPanel = new BackgroundPanel("/Main_Background.png");
        bgPanel.setLayout(new BorderLayout());
        add(bgPanel, BorderLayout.CENTER);

        buildGUI(bgPanel);
        initActions();
    }

    private void initActions() {
        b_host.addActionListener(e -> window.showScreen("create"));
        b_participation.addActionListener(e -> window.showScreen("join"));
        b_exit.addActionListener(e -> System.exit(0));
    }

    private void buildGUI(JPanel bgPanel) {
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        bgPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JPanel spacerPanel = new JPanel();
        spacerPanel.setOpaque(false);
        spacerPanel.setPreferredSize(new Dimension(1, 60));

        wrapper.add(spacerPanel, BorderLayout.NORTH);

        JPanel verticalPanel = new JPanel(new GridLayout(2, 1, 0, 40));
        verticalPanel.setOpaque(false);

        verticalPanel.add(createTitlePanel());
        verticalPanel.add(createControlPanel());

        wrapper.add(verticalPanel, BorderLayout.CENTER);

        centerPanel.add(wrapper);
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);

        ImagePanel wasdPanel = new ImagePanel("/WASD.png", 350, 150);
        ImagePanel adventurePanel = new ImagePanel("/AdventureOfBugi.png", 300, 130);

        JPanel wrap1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrap1.setOpaque(false);
        wrap1.add(wasdPanel);

        JPanel wrap2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrap2.setOpaque(false);
        wrap2.add(adventurePanel);

        titlePanel.add(wrap1);
        titlePanel.add(wrap2);

        return titlePanel;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlPanel.setOpaque(false);

        JPanel gridPanel = new JPanel(new GridLayout(3, 1, 0, 15));
        gridPanel.setOpaque(false);

        gridPanel.add(createButtonCell("호스트"));
        gridPanel.add(createButtonCell("참가하기"));
        gridPanel.add(createButtonCell("종료"));

        controlPanel.add(gridPanel);
        return controlPanel;
    }

    private JPanel createButtonCell(String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(400, 50));

        switch (text) {
            case "호스트" -> b_host = button;
            case "참가하기" -> b_participation = button;
            case "종료" -> b_exit = button;
        }

        panel.add(button);
        if ("종료".equals(text)) {
            button.addActionListener(new ExitEvent());
        }
        return panel;
    }
}
