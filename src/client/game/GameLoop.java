package client.game;

import javax.swing.*;

public class GameLoop {

    private final Timer timer;
    private final Runnable updateCallback;

    public GameLoop(Runnable updateCallback) {
        this.updateCallback = updateCallback;

        timer = new Timer(16, e -> updateCallback.run()); // 16ms = 60FPS
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }
}