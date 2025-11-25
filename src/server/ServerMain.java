package server;

public class ServerMain {

    public static void main(String[] args) {

        ServerWindow window = new ServerWindow();

        GameRoom gameRoom = new GameRoom();

        ServerHandler serverHandler = new ServerHandler(54321, gameRoom, window);
        serverHandler.start();
    }
}
