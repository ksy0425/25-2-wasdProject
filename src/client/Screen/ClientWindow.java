package client.Screen;

import client.network.ConnectionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JoinRoomScreen joinRoomScreen;
    private CreateRoomScreen createRoomScreen;
    private String serverAddress;
    private int serverPort;

    // 추가 구현
    private JPanel currentScreen;
    private long seenRoomListVersion = -1;
    private Timer roomListUiTimer;


    public ClientWindow() {
        super("WASD: 부기의 모험");

        ConnectionManager.setWindow(this);
        FileReader fr = null;
        BufferedReader br = null;

        String jarPath = "server.txt";
        String testPath = "src/resources/server.txt";

        try {
            fr = new FileReader(testPath);
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

        startRoomListUiTimer();
    }

    // 추가 구현
    private void startRoomListUiTimer() {
        roomListUiTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                var handler = ConnectionManager.getHandler();
                if (handler == null) return;

                long v = handler.getRoomListVersion();
                if (v == seenRoomListVersion) return;
                seenRoomListVersion = v;

                var rooms = handler.getRoomList();

                if (currentScreen instanceof JoinRoomScreen join) {
                    join.updateRoomList(rooms);
                } else if (currentScreen instanceof CreateRoomScreen create) {
                    create.updateRoomList(rooms);
                }
            }
        });
        roomListUiTimer.start();
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

        // 추가 구현
        currentScreen = newScreen;
        if (newScreen instanceof JoinRoomScreen join) {
            join.refreshRoomList();
        } else if (newScreen instanceof CreateRoomScreen create) {
            create.refreshRoomList();
        }


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
                createRoomScreen = new CreateRoomScreen(this);
                return createRoomScreen;
            case "join":
                joinRoomScreen = new JoinRoomScreen(this);
                return joinRoomScreen;
            case "game":
                gameScreen = new GameScreen(this);
                return gameScreen;
            default:
                throw new IllegalArgumentException("Unknown screen: " + name);
        }
    }

    public LobbyScreen getLobbyScreen() { return lobbyScreen; }
    public GameScreen getGameScreen() { return gameScreen; }
    public JoinRoomScreen getJoinRoomScreen() { return joinRoomScreen; }
    public CreateRoomScreen getCreateRoomScreen() { return createRoomScreen; }

    public int  getHostId() { return hostId; }
    public void setHostId(int hostId) { this.hostId = hostId; }
}
