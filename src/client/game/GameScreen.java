package client.game;

import client.game.input.KeyInput;
import client.game.player.Unit;

import javax.swing.*;
import java.awt.*;

public class GameScreen extends JPanel {

    private final Unit unit;
    private final KeyInput keyInput;
    private final GameLoop gameLoop;

    public GameScreen(int allowedKey) {
        setFocusable(true);

        unit = new Unit(Color.BLUE, 200, 200);
        keyInput = new KeyInput(allowedKey);
        addKeyListener(keyInput);

        gameLoop = new GameLoop(this::updateGame);
        gameLoop.start();
    }

    private void updateGame() {
        unit.isMovingTop = keyInput.isUp();
        unit.isMovingLeft = keyInput.isLeft();
        unit.isMovingBottom = keyInput.isDown();
        unit.isMovingRight = keyInput.isRight();

        unit.move(); // 기존 Unit.move() 그대로 사용
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        unit.draw(g);
    }
}
