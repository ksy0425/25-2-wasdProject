package client.network;

import client.ClientPacketHandler;
import client.ClientReceiver;
import client.Screen.ClientWindow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionManager {

    public static Socket socket;
    public static ObjectOutputStream out;
    public static ObjectInputStream in;
    private static String nickname;
    private static ClientPacketHandler handler;
    private static ClientWindow window;

    public static void setNickname(String n) {
        nickname = n;
    }
    public static String getNickname() {
        return nickname;
    }

    public static ClientPacketHandler getHandler() {
        return handler;
    }

    public static void setWindow(ClientWindow w) {
        window = w;
    }
    public static ClientWindow getWindow() {
        return window;
    }

    public static void connect(String host, int port, ClientWindow window) {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            handler = new ClientPacketHandler(window);
            new ClientReceiver(handler).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
