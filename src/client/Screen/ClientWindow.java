package client.Screen;

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
    private GameScreen gameScreen;
    private String serverAddress;
    private int serverPort;

    public ClientWindow() {
        super("WASD: 부기의 모험");

        ConnectionManager.setWindow(this);
        FileReader fr = null;
        BufferedReader br = null;

        String jarPath = "server.txt";
        //String testPath = "src/resources/server.txt";

        try {
            fr = new FileReader(jarPath);
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

        if ("game".equals(name)) {
            pack();
            setResizable(false);
        } else {
            setSize(1400, 800);
            setResizable(false);
        }
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
                gameScreen = new GameScreen(this);
                return gameScreen;
            default:
                throw new IllegalArgumentException("Unknown screen: " + name);
        }
    }

    public LobbyScreen getLobbyScreen() { return lobbyScreen; }
    public GameScreen getGameScreen() { return gameScreen; }

    public int  getHostId() { return hostId; }
    public void setHostId(int hostId) { this.hostId = hostId; }
}
