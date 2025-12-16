package client.game.obstacle;

import java.awt.*;

public class Obstacle {
    public static final int OBSTACLE_SIZE=40;
    private static final int MOVE_DISTANCE = 1;
    private Color color;
    private int x, y;
    private int preX, preY;
    public static final int LEFT = 1, RIGHT = 2, UP_DOWN = 3;
    private int direction = RIGHT;

    public Obstacle(Color color, int x, int y) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.direction = 0;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int dir) {
        this.direction = dir;
    }


//    public void startMoving(int direction) {
//        if (direction == xDistance) {
//            xDirection = -1;
//            yDirection = 0;
//        } if (direction == yDistance) {
//            xDirection = 0;
//            yDirection = -1;
//        }
//    }

//    public void draw(Graphics g) {
//        g.setColor(color);
//        g.fillRect(x, y, OBSTACLE_SIZE, OBSTACLE_SIZE);
//    }

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

    public void setX(int x) {
        if (x > this.x) direction = RIGHT;
        else if (x < this.x) direction = LEFT;
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        if (y != this.y) direction = UP_DOWN;
        this.y = y;
    }
}
