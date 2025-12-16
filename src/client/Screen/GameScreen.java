package client.Screen;

import client.ClientPacketHandler;
import client.Screen.util.GameMapPanel;
import client.game.GamePrototype;
import client.network.ConnectionManager;
import shared.model.PlayerState;

import javax.swing.*;
import java.awt.*;

public class GameScreen extends JPanel {
    private ClientWindow window;
    private ClientPacketHandler handler;
    private GamePrototype gamePrototype;

    private JPanel rolePanel;

    public GameScreen(ClientWindow window) {
        this.window = window;
        this.handler = ConnectionManager.getHandler();

        setLayout(new BorderLayout());
        setOpaque(false);

        Dimension size = new Dimension(1400, 800); // 외부 참조

        JLayeredPane overlay = new JLayeredPane();
        overlay.setOpaque(false);
        overlay.setPreferredSize(size);

        GameMapPanel mapPanel = new GameMapPanel("/GameMap.png");
        mapPanel.setLayout(new BorderLayout());
        mapPanel.setBounds(0, 0, size.width, size.height);

        gamePrototype = new GamePrototype();
        gamePrototype.setBounds(0, 0, size.width, size.height);
        mapPanel.add(gamePrototype, BorderLayout.CENTER);

        rolePanel = createRolePanel();
        rolePanel.setBounds(1310, 12, 60, 360);
        rolePanel.setOpaque(false);

        overlay.add(mapPanel, JLayeredPane.DEFAULT_LAYER);
        overlay.add(rolePanel, JLayeredPane.PALETTE_LAYER);

        add(overlay, BorderLayout.CENTER);
    }

    private JPanel createRolePanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.setOpaque(false);

        int cardW = 60;
        int cardH = 90;

        for (PlayerState ps : handler.getPlayers().values()) {
            panel.add(createPlayerRoleCard(ps.getNickname(), ps.getKeyRole(), cardW, cardH));
        }

        return panel;
    }

    private JPanel createPlayerRoleCard(String nickname, String keyRoleRaw, int w, int h) {
        JPanel card = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(w, h)); // 외부 참조

        JLabel name = new JLabel(nickname);
        name.setFont(new Font("Dialog", Font.BOLD, 20));
        name.setForeground(Color.WHITE);

        ImageIcon icon = loadKeyIcon(keyRoleRaw == null ? "" : keyRoleRaw.trim().toLowerCase());
        IconPanel iconPanel = new IconPanel(icon == null ? null : icon.getImage());
        iconPanel.setPreferredSize(new Dimension(60, 60));

        card.add(name);
        card.add(iconPanel);

        return card;
    }

    private ImageIcon loadKeyIcon(String keyRole) {
        String path = switch (keyRole) {
            case "w" -> "/W.png";
            case "a" -> "/A.png";
            case "s" -> "/S.png";
            case "d" -> "/D.png";
            default -> "/key_unknown.png";
        };

        java.net.URL url = getClass().getResource(path); // 외부 참조
        if (url == null) return null;
        return new ImageIcon(url);
    }

    private static class IconPanel extends JPanel {
        private final Image img;

        IconPanel(Image img) {
            this.img = img;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
