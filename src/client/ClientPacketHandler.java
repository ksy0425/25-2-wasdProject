// src/client/ClientPacketHandler.java
package client;

import client.Screen.ClientWindow;
import client.Screen.LobbyScreen;
import client.Screen.util.RoomPanel;
import client.network.ClientSender;
import client.network.ConnectionManager;
import shared.model.PlayerState;
import shared.packet.*;
import client.game.GamePrototype;


import javax.swing.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 클라이언트에서 서버 응답 패킷을 처리하고,
 * 플레이어 신원 정보(PlayerState)만 관리하는 핸들러.
 */
public class ClientPacketHandler {
    private ClientWindow window;

    // 나 자신 정보
    private volatile PlayerState me;

    // 현재 로비/방 등에 존재하는 플레이어들 정보 (나 포함)
    private final Map<Integer, PlayerState> players = new ConcurrentHashMap<>();

    public ClientPacketHandler(ClientWindow window) { this.window = window; }

    // -------- 외부에서 조회용 --------

    public PlayerState getMe() {
        return me;
    }

    public Map<Integer, PlayerState> getPlayers() {
        return players;
    }

    public void handle(Packet packet) {

        if (packet instanceof LoginResponsePacket p) {
            handleLoginResponse(p);

        } else if (packet instanceof CreateRoomResponsePacket p) {
            handleCreateRoomResponse(p);

        } else if (packet instanceof JoinRoomResponsePacket p) {
            handleJoinRoomResponse(p);

        } else if (packet instanceof RoomInfoPacket p) {
            handleRoomInfo(p);
        } else if (packet instanceof GameStartResponsePacket p) {
            handleGameStart(p);
        } else if (packet instanceof SyncPacket p) {
            handleSync(p);
        } else {
            System.out.println("[CLIENT] 알 수 없는 패킷 수신: " + packet.getClass().getSimpleName());
        }
    }

    private void handleLoginResponse(LoginResponsePacket packet) {
        if(packet.isAccepted()) {
            int playerId = packet.getPlayerId();
            String nickname = packet.getNickname();

            PlayerState myState = new PlayerState(nickname, playerId);
            me = myState;
            players.put(playerId, myState);

            ConnectionManager.setNickname(nickname);

            System.out.println("[CLIENT] 로그인 성공: " + myState);

            window.showScreen("main");
        }
        else {
            window.alert(packet.getReason());
        }
    }

    private void handleCreateRoomResponse(CreateRoomResponsePacket packet) {
        boolean ok = packet.isAccepted();
        String msg = packet.getReason();
        int hostId = packet.getHostId();

        if (ok) {
            System.out.println("[CLIENT] 방 생성 성공");
            window.setHostId(hostId);
            System.out.println("hostId : " + hostId);
            window.setRoomTitle(packet.getRoomTitle());
            window.showScreen("lobby");
        } else {
            System.out.println("[CLIENT] 방 생성 실패: " + msg);
            JOptionPane.showMessageDialog(null, msg, "알림", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleJoinRoomResponse(JoinRoomResponsePacket packet) {
        boolean ok = packet.isAccepted();
        String msg = packet.getReason();
        int hostId = packet.getHostId();

        if (ok) {
            System.out.println("[CLIENT] 방 참가 성공");
            window.setHostId(hostId);
            window.setRoomTitle(packet.getRoomTitle());
            window.showScreen("lobby");
        } else {
            System.out.println("[CLIENT] 방 참가 실패: " + msg);
        }
    }

    private void handleRoomInfo(RoomInfoPacket packet) {
        int hostId = window.getHostId();
        System.out.println("window.getHostId() : " + hostId);
        if (hostId == -1) return;

        players.clear();
        boolean isBoom = true;
        for (PlayerState ps : packet.getPlayers()) {
            if(ps.getPlayerId() == hostId) {
                isBoom = false;
            }
            players.put(ps.getPlayerId(), ps);
        }

        System.out.println("isBoom : " + isBoom);
        System.out.println("[CLIENT] 방 정보 갱신: "
                + packet.getRoomTitle() + " / 인원 = " + players.size());

        if (isBoom) {
            players.clear();

            window.setHostId(-1);
            ClientSender.send(new LeaveRoomPacket());
            window.showScreen("main");
            JOptionPane.showMessageDialog(window,"호스트가 방을 나갔습니다.","알림",JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            window.showScreen("lobby");
        }
    }

    public void handleGameStart(GameStartResponsePacket p) {
        RoomPanel roomPanel = window.getLobbyScreen().getRoomPanel();
        Map<Integer, String> playersKey = p.getPlayersKey();

        for (Map.Entry<Integer, String> entry : playersKey.entrySet()) {
            Integer playerId = entry.getKey();   // 서버에서 내려준 playerId
            String keyRole = entry.getValue();   // ex) "W", "A", "S", "D"

            System.out.println("[DEBUG] GameStart: playerId=" + playerId + ", keyRole='" + keyRole + "'");

            PlayerState ps = players.get(playerId);  // players 맵에서 찾기
            if (ps != null) {
                ps.setKeyRole(keyRole);          // players 쪽 객체에 키 역할 세팅

                // ★ me도 같이 업데이트 (같은 사람이면)
                if (me != null && me.getPlayerId() == playerId) {
                    me.setKeyRole(keyRole);
                    System.out.println("[DEBUG] me 업데이트: id=" + me.getPlayerId()
                            + ", keyRole='" + me.getKeyRole() + "'");
                }
            } else {
                System.out.println("[DEBUG] handleGameStart: players 맵에서 playerId=" + playerId + " 찾기 실패");
            }
        }
        window.showScreen("lobby");

        // 호스트, 클라이언트 시작 버튼 강제 활성화 후  시간 표시
        LobbyScreen lobby = window.getLobbyScreen();
        lobby.startButton.setVisible(true);
        for (int i=10; i>0; i--) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lobby.startButton.setText(i + "초 후 시작");
            }
        }
        window.showScreen("game");
    }

    private void handleSync(SyncPacket packet) {
        GamePrototype gp = GamePrototype.getInstance();
        if (gp != null) {
            gp.updateUnitPosition(packet.getX(), packet.getY());
            gp.updateObstarclePosition(packet.getOx(), packet.getOy());
        }
        //System.out.println("====" + packet.getX() + ", " + packet.getY()+"====");
    }

    public void onDisconnected() {
        System.out.println("[CLIENT] 서버와의 연결이 끊어졌습니다.");
        // TODO: 팝업 띄우고 로그인 화면으로 되돌리기 등
    }
}
