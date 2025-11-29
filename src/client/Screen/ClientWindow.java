package client.Screen;

import client.network.ConnectionManager;

import javax.swing.*;
import java.awt.*;

public class ClientWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel container;
    private String roomTitle;

    public ClientWindow() {
        super("WASD: 부기의 모험");

        ConnectionManager.connect("localhost", 54321);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        showScreen("login");

        add(container);

        setSize(1400, 800);
        setLocation(100, 10);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    public String getRoomTitle() {
        return this.roomTitle;
    }

    public void showScreen(String name) {
        JPanel newScreen = createScreen(name);

        container.removeAll();
        container.add(newScreen, name);
        cardLayout.show(container, name);
        container.revalidate();
        container.repaint();
    }

    private JPanel createScreen(String name) {
        switch (name) {
            case "login":
                return new LoginScreen(this);
            case "main":
                return new MainScreen(this);
            case "host":
                return new HostScreen(this);
            case "create":
                return new CreateRoomScreen(this);
            case "participation":
                return new ParticipationScreen(this);
            case "join":
                return new JoinRoomScreen(this);
            default:
                throw new IllegalArgumentException("Unknown screen: " + name);
        }
    }
}
