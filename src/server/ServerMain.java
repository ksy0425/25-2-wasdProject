package server;

import javax.swing.*;

public class ServerMain {
    private int port;

    public ServerMain(int port) {

        this.port = port;
    }

    public static void main(String[] args) {
        new ServerWindow();
    }
}
