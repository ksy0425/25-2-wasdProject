package client.Screen.util;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    private Image background;

    public BackgroundPanel(String path) {
        background = new ImageIcon(getClass().getResource(path)).getImage(); // 외부 참조
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
    }
}
