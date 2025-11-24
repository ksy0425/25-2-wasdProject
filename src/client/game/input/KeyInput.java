package client.game.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {

    private boolean up, left, down, right;
    private final int allowedKey;

    public KeyInput(int allowedKey) {
        this.allowedKey = allowedKey;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == allowedKey) {
            switch (allowedKey) {
                case KeyEvent.VK_W -> up = true;
                case KeyEvent.VK_A -> left = true;
                case KeyEvent.VK_S -> down = true;
                case KeyEvent.VK_D -> right = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == allowedKey) {
            switch (allowedKey) {
                case KeyEvent.VK_W -> up = false;
                case KeyEvent.VK_A -> left = false;
                case KeyEvent.VK_S -> down = false;
                case KeyEvent.VK_D -> right = false;
            }
        }
    }

    public boolean isUp() { return up; }
    public boolean isLeft() { return left; }
    public boolean isDown() { return down; }
    public boolean isRight() { return right; }
}
