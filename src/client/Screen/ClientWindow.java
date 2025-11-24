package client.Screen;

import javax.swing.*;
import java.awt.*;

public class ClientWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel container;

    public ClientWindow() {
        super("WASD: 부기의 모험");

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        // 화면 등록
        container.add(new MainScreen(this), "main");
        container.add(new HostScreen(this, "방 정보"), "host");
        container.add(new CreateRoomScreen(this), "create");
        container.add(new ParticipationScreen(this), "participation");

        add(container);

        setSize(1400, 800);
        setLocation(100, 10);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void showScreen(String name) {
        cardLayout.show(container, name);
    }
}

