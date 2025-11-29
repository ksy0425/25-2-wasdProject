package server;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerHandler extends Thread {

    private final int port;
    private final RoomManager roomManager;
    private final ServerWindow window;

    public ServerHandler(int port, RoomManager roomManager, ServerWindow window) {
        this.port = port;
        this.roomManager = roomManager;
        this.window = window;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            window.printDisplay("서버가 포트 " + port + "에서 시작되었습니다.");
            System.out.println("[SERVER] listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                window.printDisplay("클라이언트 접속: " + clientSocket.getInetAddress());
                System.out.println("[SERVER] client connected: " + clientSocket);

                ClientHandler handler = new ClientHandler(clientSocket, roomManager, window);
                handler.start();
            }

        } catch (Exception e) {
            window.printDisplay("서버 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
