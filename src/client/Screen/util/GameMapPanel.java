package client.Screen.util;

import javax.swing.*;
import java.awt.*;

public class GameMapPanel extends JPanel {
    private Image gameMap;

    public GameMapPanel(String path) {
        gameMap = new ImageIcon(getClass().getResource(path)).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(gameMap, 0, 0, this);
    }
}
