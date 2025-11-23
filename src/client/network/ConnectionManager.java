package client.network;

import client.ClientReceiver;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionManager {

    public static Socket socket;
    public static ObjectOutputStream out;
    public static ObjectInputStream in;

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
