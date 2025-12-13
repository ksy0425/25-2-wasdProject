package client.game.obstacle;

import java.awt.*;

public class Obstacle {
    public static final int UNIT_SIZE=40;
    private static final int MOVE_DISTANCE = 1;
    private Color color;
    public int x, y;
    private int preX, preY;
    public static final int xDistance = 1, yDistance = 2;
    private int xDirection, yDirection;

    public Obstacle(Color color, int x, int y) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.xDirection = 0;
        this.yDirection = 0;
    }

    public void startMoving(int direction) {
        if (direction == xDistance) {
            xDirection = -1;
            yDirection = 0;
        } if (direction == yDistance) {
            xDirection = 0;
            yDirection = -1;
        }
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, UNIT_SIZE, UNIT_SIZE);
    }

//    public void move() {
//        this.preX = x;
//        this.preY = y;
//        x += xDirection * MOVE_DISTANCE;
//        y += yDirection * MOVE_DISTANCE;
//
//    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
