package client.Screen;

import client.ClientPacketHandler;
import client.Screen.util.BackgroundPanel;
import client.Screen.util.GameMapPanel;
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

        setLayout(new BorderLayout());
        setOpaque(false);

        GameMapPanel mapPanel = new GameMapPanel("/GameMap.png");
        mapPanel.setLayout(new BorderLayout());

        gamePrototype = new GamePrototype();

        // ⭐️ 사이즈는 "최상위에" 주는 게 제일 확실함
        setPreferredSize(new Dimension(1400, 800));
        mapPanel.setPreferredSize(new Dimension(1400, 800));
        gamePrototype.setPreferredSize(new Dimension(1400, 800));

        add(mapPanel, BorderLayout.CENTER);
        mapPanel.add(gamePrototype, BorderLayout.CENTER);
    }
}
