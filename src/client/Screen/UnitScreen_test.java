package client.Screen;

import client.game.GameLoop;
import client.game.input.KeyInput;
import client.game.player.Unit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class UnitScreen_test extends JPanel {

    private Unit unit;
    private KeyInput input;
    private GameLoop loop;

    public UnitScreen_test() {
        setFocusable(true);

        unit = new Unit(Color.BLUE, 200, 200);
        input = new KeyInput(KeyEvent.VK_D); // ← D만 테스트
        addKeyListener(input);

        loop = new GameLoop(this::updateGame);
        loop.start();
    }

    private void updateGame() {
        unit.isMovingTop = input.isUp();
        unit.isMovingLeft = input.isLeft();
        unit.isMovingBottom = input.isDown();
        unit.isMovingRight = input.isRight();

        unit.move();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        unit.draw(g);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Unit test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        UnitScreen_test test = new UnitScreen_test();
        frame.add(test);

        frame.setSize(800, 600);
        frame.setVisible(true);

        test.requestFocusInWindow();
    }
}

