package client.Screen;

import client.ClientPacketHandler;
import client.Screen.util.BackgroundPanel;
import client.game.GamePrototype;
import client.game.player.Unit;
import client.network.ConnectionManager;

import javax.swing.*;
import java.awt.*;

public class GameScreen extends JPanel {
    private ClientWindow window;
    private ClientPacketHandler handler;
    private GamePrototype gamePrototype;

    private Unit unit;
    private JButton b_exit;

    public GameScreen(ClientWindow window) {
        this.window = window;
        this.handler = ConnectionManager.getHandler();
        setOpaque(false);

        buildGUI();
    }

    public void buildGUI() {
        gamePrototype = new GamePrototype();

        setLayout(new BorderLayout());
        this.add(gamePrototype, BorderLayout.CENTER);

        gamePrototype.start();
    }

}
