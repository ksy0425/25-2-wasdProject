package client.KeyEvent;

import client.network.ConnectionManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ExitEvent implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (ConnectionManager.out != null) {
                ConnectionManager.out.close();
            }
            if (ConnectionManager.in != null) {
                ConnectionManager.in.close();
            }
            if (ConnectionManager.socket != null && !ConnectionManager.socket.isClosed()) {
                ConnectionManager.socket.close();
            }
        } catch (IOException ex) {
            System.out.println("[CLIENT] 종료 중 예외 발생: " + ex.getMessage());
        }
        System.out.println("[CLIENT] 종료 완료. 프로그램 종료.");
        System.exit(0);
    }
}
