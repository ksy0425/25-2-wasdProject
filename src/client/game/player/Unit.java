package client.game.player;

import java.awt.Color;
import java.awt.Graphics;

public class Unit {

    private int UNIT_SIZE = 40;
    private int MOVE_DISTANCE = 5;

    private Color color;
    public int x, y;

    public boolean isMovingTop, isMovingBottom, isMovingLeft, isMovingRight;

    public Unit(Color color, int x, int y) {
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, UNIT_SIZE, UNIT_SIZE);
    }

    public void move() {

        if (isMovingLeft)  x -= MOVE_DISTANCE;
        if (isMovingRight) x += MOVE_DISTANCE;
        if (isMovingTop)   y -= MOVE_DISTANCE;
        if (isMovingBottom)y += MOVE_DISTANCE;

        // 경계 체크
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        int maxX = 800 - UNIT_SIZE;
        int maxY = 600 - UNIT_SIZE;
        if (x > maxX) x = maxX;
        if (y > maxY) y = maxY;
    }
}
