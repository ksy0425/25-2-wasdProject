package client.game;

import client.game.obstacle.Obstacle;
import client.game.player.Unit;
import client.network.ClientSender;
import client.network.ConnectionManager;
import shared.model.PlayerState;
import shared.packet.MovePacket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GamePrototype extends JComponent {

    // SyncPacket에서 접근하기 위한 싱글톤 레퍼런스
    private static GamePrototype instance;

    private final Unit unit;
    private Obstacle obstacleX, obstacleY;
    private final int myPlayerId;
    private final String keyRole; // "w", "a", "s", "d"

    public GamePrototype() {
        instance = this;

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

        // 유닛 초기 위치 (적당히 조정 가능)
        unit = new Unit(Color.RED, 200, 200);
        //장애물 생성
        obstacleX = new Obstacle(Color.BLACK, 100, 400);
        obstacleY = new Obstacle(Color.BLACK, 600, 100);
        //obstarcle.startMoving(1);

        setFocusable(true);
        setOpaque(false);
        setRequestFocusEnabled(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e);
            }
        });
    }

    public static GamePrototype getInstance() {
        return instance;
    }

    public void start() {
        requestFocusInWindow();
    }

    private void handleKeyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        System.out.println(
                "[DEBUG] keyPressed: " + KeyEvent.getKeyText(code) +
                        ", myPlayerId=" + myPlayerId +
                        ", keyRole='" + keyRole + "'"
        );

        if (myPlayerId < 0) return;

        int dir = -1;

        // 내게 부여된 키 역할에 따라 어떤 방향 패킷을 보낼지 결정
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
            ClientSender.send(new MovePacket(myPlayerId, dir));
        }
    }

    // 서버에서 SyncPacket으로 보내준 좌표를 반영
    public void updateUnitPosition(int x, int y) {
        //System.out.println("[GAME] updateUnitPosition: " + x + ", " + y);
        unit.x = x;
        unit.y = y;
        repaint();
    }

    public void updateObstarclePosition(int x, int y) {
        obstacleX.x = x;
        //obstarcleX.y = y;
        //obstarcleY.x = x;
        obstacleY.y = y;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        unit.draw(g);
        obstacleX.draw(g);
        obstacleY.draw(g);
    }

    // 필요하다면 외부에서 호출할 stop() (일시 정지는 크게 신경 안써도 된다고 해서 간단히 처리)
    public void stop() {
        if (myPlayerId >= 0) {
            ClientSender.send(new MovePacket(myPlayerId, MovePacket.STOP));
        }
    }

    public void addNotify() {
        super.addNotify();
        // 화면에 붙는 순간 포커스 시도
        requestFocusInWindow();
    }
}
