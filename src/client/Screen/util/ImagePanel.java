package client.Screen.util;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {

    private Image image;
    private int width;
    private int height;

    public ImagePanel(String resourcePath, int width, int height) {
        this.width = width;
        this.height = height;

        ImageIcon icon = new ImageIcon(getClass().getResource(resourcePath));
        this.image = icon.getImage();

        setPreferredSize(new Dimension(width, height));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(image, 0, 0, width, height, this);
    }
}

