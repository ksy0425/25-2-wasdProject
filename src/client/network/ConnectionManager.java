package client.network;

import client.ClientReceiver;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionManager {

    public static Socket socket;
    public static ObjectOutputStream out;
    public static ObjectInputStream in;
    private static String nickname;

    public static void setNickname(String n) {
        nickname = n;
    }
    public static String getNickname() {
        return nickname;
    }

    public static void connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            new ClientReceiver().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
