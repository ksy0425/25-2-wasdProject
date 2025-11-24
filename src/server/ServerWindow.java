package server;

import javax.swing.*;
import java.awt.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerWindow  extends JFrame {
    private JTextArea t_display;
    private ServerHandler serverHandler;

    public ServerWindow() {
        super("Server");

        buildGUI();
        setSize(400, 1000);
        setLocation(1000, 0);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        // 서버 시작
        startServer();
    }

    private void buildGUI() {
        add(DisplayPanel(), BorderLayout.CENTER);
    }
    private JPanel DisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        t_display = new JTextArea();
        t_display.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(t_display);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    public void startServer() {
        System.out.println("서버 시작...");
        printDisplay("서버가 시작되었습니다.\n");
        try (ServerSocket serverSocket = new ServerSocket(54321)) {
            //GameRoom gameRoom = new GameRoom();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("클라이언트가 연결되었습니다...");
                printDisplay("클라이언트가 연결되었습니다...");

                ServerHandler handler = new ServerHandler(clientSocket);
                handler.start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void printDisplay(String str){
        t_display.append(str +"\n");
    }

}
