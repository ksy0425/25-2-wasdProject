package client.Screen.util;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    private Image background;

    public BackgroundPanel(String path) {
        background = new ImageIcon(getClass().getResource(path)).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 프레임 크기에 맞게 배경 채우기
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
    }
}
