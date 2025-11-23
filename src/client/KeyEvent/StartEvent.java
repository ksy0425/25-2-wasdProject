package client.KeyEvent;

import client.network.ConnectionManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartEvent implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        ConnectionManager.connect("localhost", 54321);
    }
}
