package client.game.player;

import java.awt.Color;
import java.awt.Graphics;

public class Unit {

    public static final int UNIT_SIZE=40;
    private static final int MOVE_DISTANCE = 1;
    private Color color;
    public int x, y;
    private int preX, preY;
    public static final int LEFT = 1, RIGHT = 2, UP = 3, DOWN = 4;
    private int xDirection, yDirection;

    public Unit (Color color, int x, int y) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.xDirection = 0;
        this.yDirection = 0;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, UNIT_SIZE, UNIT_SIZE);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

//    public void startMoving(int direction) {
//        if (direction == LEFT) {
//            xDirection = -1;
//            yDirection = 0;
//        } if (direction == RIGHT) {
//            xDirection = 1;
//            yDirection = 0;
//        } if (direction == UP) {
//            xDirection = 0;
//            yDirection = -1;
//        } if (direction == DOWN) {
//            xDirection = 0;
//            yDirection = 1;
//        }
//    }
//
//    public void stopMoving() {
//        this.xDirection = 0;
//        this.yDirection =0;
//    }
//
//    public void move() {
//        this.preX = x;
//        this.preY = y;
//        x += xDirection * MOVE_DISTANCE;
//        y += yDirection * MOVE_DISTANCE;
//
//    }
//
//    public void moveBack() {
//        x = preX;
//        y = preY;
//    }
}
