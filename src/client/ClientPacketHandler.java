// src/client/ClientPacketHandler.java
package client;

import client.Screen.ClientWindow;
import client.network.ConnectionManager;
import shared.model.PlayerState;
import shared.packet.*;

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
        } else if (packet instanceof PlayerLeftRoomPacket p) {
            handlePlayerLeftRoom(p);
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

        if (ok) {
            System.out.println("[CLIENT] 방 생성 성공");
            window.showScreen("host");
        } else {
            System.out.println("[CLIENT] 방 생성 실패: " + msg);
            JOptionPane.showMessageDialog(null, msg, "알림", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleJoinRoomResponse(JoinRoomResponsePacket packet) {
        boolean ok = packet.isAccepted();
        String msg = packet.getReason();

        if (ok) {
            System.out.println("[CLIENT] 방 참가 성공");
            // TODO: 방 내부 화면 전환
        } else {
            System.out.println("[CLIENT] 방 참가 실패: " + msg);
        }
    }

    private void handleRoomInfo(RoomInfoPacket packet) {
        players.clear();
        for (PlayerState ps : packet.getPlayers()) {
            players.put(ps.getPlayerId(), ps);
        }

        System.out.println("[CLIENT] 방 정보 갱신: "
                + packet.getRoomTitle() + " / 인원 = " + players.size());

        // UI는 EDT에서 갱신
        SwingUtilities.invokeLater(() -> {
            // 아래에서 설명할 HostScreen.refreshParticipants() 호출
            client.Screen.ClientWindow window = ConnectionManager.getWindow(); // static으로 하나 보관
            if (window != null) {
                window.refreshRoomView(); // 또는 window.getHostScreen().refreshParticipants();
            }
        });
    }

    private void handlePlayerLeftRoom(PlayerLeftRoomPacket packet) {
        int playerId = packet.getPlayerId();
        players.remove(playerId);

        System.out.println("[CLIENT] 플레이어 퇴장: playerId=" + playerId);
        // TODO: UI에서 해당 플레이어 제거 후 리렌더
    }

    public void onDisconnected() {
        System.out.println("[CLIENT] 서버와의 연결이 끊어졌습니다.");
        // TODO: 팝업 띄우고 로그인 화면으로 되돌리기 등
    }
}
