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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientPacketHandler {
    private ClientWindow window;

    private volatile PlayerState me; // 외부 참조

    private final Map<Integer, PlayerState> players = new ConcurrentHashMap<>();

    // 추가 구현
    private volatile Map<String, Integer> roomList = Collections.emptyMap();
    public Map<String, Integer> getRoomList() {
        return roomList;
    }
    private volatile long roomListVersion = 0;
    public long getRoomListVersion() { return roomListVersion; }

    public ClientPacketHandler(ClientWindow window) { this.window = window; }

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
        } else if (packet instanceof RoomListResponsePacket p) { // 추가 구현
            handleRoomListResponse(p);
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
            JOptionPane.showMessageDialog(window, "해당 닉네임이 이미 존재합니다.", "알림", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(null, msg, "알림", JOptionPane.WARNING_MESSAGE);
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
        if (!(p.isAccepted())) {
            JOptionPane.showMessageDialog(window, "현재 방 인원이 4명이 아닙니다.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }
        RoomPanel roomPanel = window.getLobbyScreen().getRoomPanel();
        Map<Integer, String> playersKey = p.getPlayersKey();

        for (Map.Entry<Integer, String> entry : playersKey.entrySet()) {
            Integer playerId = entry.getKey();
            String keyRole = entry.getValue();

            System.out.println("[DEBUG] GameStart: playerId=" + playerId + ", keyRole='" + keyRole + "'");

            PlayerState ps = players.get(playerId);
            if (ps != null) {
                ps.setKeyRole(keyRole);

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

        LobbyScreen lobby = window.getLobbyScreen();
        lobby.startButton.setVisible(true);
        lobby.startButton.setEnabled(false);
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
            gp.updateUnitState(packet.getX(), packet.getY(), packet.getDir());
            gp.updateObstarclePosition(packet.getOx(), packet.getOy());
        }
        gp.updateElapsedMs(packet.getElapsedMs());

        if (packet.getIsFinished()) {
            JOptionPane.showMessageDialog(window,
                    "기록: " + formatMs(packet.getElapsedMs()),
                    "CLEAR!",
                    JOptionPane.INFORMATION_MESSAGE
            );
            window.showScreen("lobby");
        }
    }

    // 추가 구현
    private void handleRoomListResponse(RoomListResponsePacket packet) {
        Map<String, Integer> next = new LinkedHashMap<>(packet.getRooms());
        if (next.equals(this.roomList))
            return;
        this.roomList = next;
        this.roomListVersion++;
        System.out.println("[CLIENT] 방 목록 수신:");
        for (Map.Entry<String, Integer> e : roomList.entrySet()) {
            System.out.println(" - " + e.getKey() + " (" + e.getValue() + "/4)");
        }
    }

    private String formatMs(long ms) {
        long m = ms / 60000;
        long s = (ms % 60000) / 1000;
        long r = ms % 1000;
        return String.format("%02d:%02d.%03d", m, s, r);
    }

    public void onDisconnected() {
        System.out.println("[CLIENT] 서버와의 연결이 끊어졌습니다.");
    }
}
