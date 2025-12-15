package client.Screen;

import client.Screen.util.GameMapPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;

public class TestScreen extends JFrame {
    public TestScreen() {
        setBounds(0,0,1400,800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        GameMapPanel mapPanel = new GameMapPanel("/GameMap.png");
        mapPanel.setLayout(new BorderLayout());
        add(mapPanel, BorderLayout.CENTER);
        setVisible(true);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("x: " + e.getPoint().getX() + " y:" + e.getPoint().getY());
            }
        });
    }
    public static void main(String[] args) {
        new TestScreen();
    }
}
