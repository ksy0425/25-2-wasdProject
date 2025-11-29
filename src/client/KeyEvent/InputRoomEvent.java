package client.KeyEvent;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InputRoomEvent extends MouseAdapter {
    private JTextField textField;
    public InputRoomEvent(JTextField textField) {
        this.textField = textField;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        textField.setText("");
        textField.removeMouseListener(this);
    }

}