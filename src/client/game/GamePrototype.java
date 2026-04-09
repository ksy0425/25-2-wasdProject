package client.game;

import client.game.obstacle.Obstacle;
import client.game.player.Unit;
import client.network.ClientSender;
import client.network.ConnectionManager;
import shared.model.PlayerState;
import shared.packet.MovePacket;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class GamePrototype extends JComponent {

    private static GamePrototype instance;

    private final Unit unit;
    private Obstacle obstacleX, obstacleY;
    private final int myPlayerId;
    private final String keyRole;
    private int dir;
    private volatile long elapsedMs = 0; // 외부 참조

    private final Map<Integer, BufferedImage> unitSprites = new HashMap<>(); // 외부 참조
    private final Map<Integer, BufferedImage> obstacles = new HashMap<>(); // 외부 참조

    public GamePrototype() {
        instance = this;

        loadUnitSprites();
        loadObstacleSprites();

        PlayerState me = ConnectionManager.getHandler().getMe();
        if (me != null) {
            this.myPlayerId = me.getPlayerId();
            this.keyRole = me.getKeyRole() == null ? "" : me.getKeyRole().toLowerCase();
            System.out.println("[GAME] me: id=" + myPlayerId + ", raw keyRole='" + keyRole + "'");
            System.out.println("[GAME] me: normalized keyRole='" + keyRole + "'");
        } else {
            System.out.println("[GAME] me is null");
            this.myPlayerId = -1;
            this.keyRole = "";
        }

        int spawnX = (int) Math.round(((85.0 + 210.0) / 2.0) - (Unit.UNIT_SIZE_WIDTH / 2.0));
        int spawnY = (int) Math.round(((674.0 + 782.0) / 2.0) - (Unit.UNIT_SIZE_HEIGHT / 2.0));
        unit = new Unit(spawnX, spawnY);

        obstacleX = new Obstacle(Color.BLACK, 390, 280);
        obstacleY = new Obstacle(Color.BLACK, 1120, 210);
        obstacleX.setDirection(Obstacle.RIGHT);
        obstacleY.setDirection(Obstacle.UP_DOWN);

        setFocusable(true);
        setOpaque(false);
        setRequestFocusEnabled(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("x: " + e.getPoint().getX() + " y:" + e.getPoint().getY());
            }
        });
    }

    private BufferedImage loadSprite(String path) { // 외부 참조
        try {
            return ImageIO.read(getClass().getResource(path));
        } catch (Exception e) {
            throw new RuntimeException("스프라이트 로딩 실패: " + path, e);
        }
    }

    public void loadUnitSprites() {
        unitSprites.put(Unit.UP,    loadSprite("/up.png"));
        unitSprites.put(Unit.DOWN,  loadSprite("/down.png"));
        unitSprites.put(Unit.LEFT,  loadSprite("/left.png"));
        unitSprites.put(Unit.RIGHT, loadSprite("/right.png"));
    }

    public void loadObstacleSprites() {
        obstacles.put(Obstacle.LEFT, loadSprite("/Left_Eagle.png"));
        obstacles.put(Obstacle.RIGHT, loadSprite("/Right_Eagle.png"));
        obstacles.put(Obstacle.UP_DOWN, loadSprite("/Up_Down_Eagle.png"));
    }

    public static GamePrototype getInstance() {
        return instance;
    }

    private void handleKeyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        System.out.println(
                "[DEBUG] keyPressed: " + KeyEvent.getKeyText(code) +
                        ", myPlayerId=" + myPlayerId +
                        ", keyRole='" + keyRole + "'"
        );

        if (myPlayerId < 0) return;

        dir = -1;

        switch (keyRole) {
            case "w" -> {
                System.out.println("[DEBUG] case 'w' 진입, code=" + code);
                if (code == KeyEvent.VK_W) {
                    System.out.println("[DEBUG] W 키 눌림 → UP");
                    dir = MovePacket.UP;
                }
            }
            case "a" -> {
                System.out.println("[DEBUG] case 'a' 진입, code=" + code);
                if (code == KeyEvent.VK_A) {
                    System.out.println("[DEBUG] A 키 눌림 → LEFT");
                    dir = MovePacket.LEFT;
                }
            }
            case "s" -> {
                System.out.println("[DEBUG] case 's' 진입, code=" + code);
                if (code == KeyEvent.VK_S) {
                    System.out.println("[DEBUG] S 키 눌림 → DOWN");
                    dir = MovePacket.DOWN;
                }
            }
            case "d" -> {
                System.out.println("[DEBUG] case 'd' 진입, code=" + code);
                if (code == KeyEvent.VK_D) {
                    System.out.println("[DEBUG] D 키 눌림 → RIGHT");
                    dir = MovePacket.RIGHT;
                }
            }
            default -> {
                System.out.println("[DEBUG] switch default: keyRole='" + keyRole + "'");
            }
        }
        System.out.println("[DEBUG] dir 계산 결과 = " + dir);
        if (dir != -1) {
            System.out.println("[DEBUG] send MovePacket, dir=" + dir + ", myId=" + myPlayerId);

            if (unit != null) unit.setFacing(dir);
            ClientSender.send(new MovePacket(myPlayerId, dir));
        }
    }

    public void updateUnitState(int x, int y, int dir) {
        unit.x = x;
        unit.y = y;
        unit.setFacing(dir);
        repaint();
    }

    public void updateObstarclePosition(int x, int y) {
        obstacleX.setX(x);
        obstacleY.setY(y);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (unit == null) return;

        BufferedImage img = unitSprites.getOrDefault(unit.getFacing(), unitSprites.get(Unit.DOWN));
        g.drawImage(img, unit.getX(), unit.getY(), Unit.UNIT_SIZE_WIDTH, Unit.UNIT_SIZE_HEIGHT, null);

        img = obstacles.getOrDefault(obstacleX.getDirection(), obstacles.get(Obstacle.RIGHT));
        g.drawImage(img, obstacleX.getX(), obstacleX.getY(), Obstacle.OBSTACLE_SIZE, Obstacle.OBSTACLE_SIZE, null);
        img = obstacles.getOrDefault(obstacleY.getDirection(), obstacles.get(Obstacle.UP_DOWN));
        g.drawImage(img, obstacleY.getX(), obstacleY.getY(), Obstacle.OBSTACLE_SIZE, Obstacle.OBSTACLE_SIZE, null);

        String text = formatMs(elapsedMs);

        g.setFont(new Font("Dialog", Font.BOLD, 18));
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRoundRect(10, 10, 120, 28, 12, 12);

        g.setColor(Color.WHITE);
        g.drawString(text, 18, 30);
        g.dispose();
    }

    public void updateElapsedMs(long ms) {
        this.elapsedMs = ms;
        repaint();
    }

    private String formatMs(long ms) {
        long m = ms / 60000;
        long s = (ms % 60000) / 1000;
        long r = ms % 1000;
        return String.format("%02d:%02d.%03d", m, s, r);
    }
}
