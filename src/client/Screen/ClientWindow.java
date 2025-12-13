package client.Screen;

import client.Screen.util.PopupPanel;
import client.network.ConnectionManager;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ClientWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel container;
    private String roomTitle;
    private int hostId;
    private LobbyScreen lobbyScreen;
    private String serverAddress;
    private int serverPort;

    public ClientWindow() {
        super("WASD: 부기의 모험");

        ConnectionManager.setWindow(this);
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader("src/resources/server.txt");
            br = new BufferedReader(fr);
            serverAddress = br.readLine();
            serverPort = Integer.parseInt(br.readLine());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ConnectionManager.connect(serverAddress, serverPort, this);

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

    public void alert(String message) {
        PopupPanel popup = new PopupPanel(message);

        container.add(popup, "popup");
        cardLayout.show(container, "popup");
    }

    private JPanel createScreen(String name) {
        switch (name) {
            case "login":
                return new LoginScreen(this);
            case "main":
                return new MainScreen(this);
            case "lobby":
                lobbyScreen = new LobbyScreen(this);
                return lobbyScreen;
            case "create":
                return new CreateRoomScreen(this);
            case "join":
                return new JoinRoomScreen(this);
            case "game":
                return new GameScreen(this);
            default:
                throw new IllegalArgumentException("Unknown screen: " + name);
        }
    }

    // 추가: 방 참가자 목록을 다시 그리도록 HostScreen에 요청하는 헬퍼
    public void refreshRoomView() {
        if (lobbyScreen != null) {
            lobbyScreen.refreshParticipants();
        }
    }

    public LobbyScreen getLobbyScreen() { return lobbyScreen; }

    public int  getHostId() { return hostId; }
    public void setHostId(int hostId) { this.hostId = hostId; }
}
