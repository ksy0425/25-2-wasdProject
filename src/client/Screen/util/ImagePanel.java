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

        // 리소스 기반 이미지 로드
        ImageIcon icon = new ImageIcon(getClass().getResource(resourcePath));
        this.image = icon.getImage();

        setPreferredSize(new Dimension(width, height));
        setOpaque(false); // 배경 투명
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 원하는 크기에 맞춰 그리기
        g.drawImage(image, 0, 0, width, height, this);
    }
}

