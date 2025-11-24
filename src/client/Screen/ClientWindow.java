package client.Screen;

import client.network.ConnectionManager;

import javax.swing.*;
import java.awt.*;

public class ClientWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel container;
    private String roomTitle;  // 방 제목을 저장할 변수

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

    // 방 제목을 설정하는 메서드
    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    // 방 제목을 가져오는 메서드
    public String getRoomTitle() {
        return this.roomTitle;
    }

    public void showScreen(String name) {
        // 해당 화면을 동적으로 생성
        JPanel newScreen = createScreen(name);

        // 화면을 새로 추가하고 전환
        container.removeAll();  // 기존 화면 제거
        container.add(newScreen, name);
        cardLayout.show(container, name);
        container.revalidate();  // 레이아웃을 재계산
        container.repaint();  // 화면을 다시 그리기
    }

    private JPanel createScreen(String name) {
        switch (name) {
            case "login":
                return new LoginScreen(this);
            case "main":
                return new MainScreen(this);
            case "host":
                return new HostScreen(this);  // 방 제목을 HostScreen으로 전달
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
