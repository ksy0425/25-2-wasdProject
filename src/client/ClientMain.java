package client;

import client.Screen.MainScreen;

public class ClientMain {
    private String serverAddress, userId;
    private int serverPort;

    public ClientMain(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }
    public static void main(String[] args) {
        new MainScreen();
    }
}
