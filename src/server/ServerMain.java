package server;

public class ServerMain {

    public static void main(String[] args) {

        ServerWindow window = new ServerWindow();

        RoomManager roomManager = new RoomManager(window);

        ServerHandler serverHandler = new ServerHandler(54321, roomManager, window);
        serverHandler.start();
    }
}
