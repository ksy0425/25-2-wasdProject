package client.KeyEvent;

import client.Screen.ClientWindow;
import client.network.ClientSender;
import client.network.ConnectionManager;
import server.ClientHandler;
import shared.model.PlayerState;
import shared.packet.JoinRoomRequestPacket;
import shared.packet.LeaveRoomPacket;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LeaveRoomEvent implements ActionListener {
    private ClientWindow window;

    public LeaveRoomEvent(ClientWindow window) {
        this.window = window;
    }
    @Override
    public void actionPerformed(ActionEvent e) {

        ClientSender.send(new LeaveRoomPacket());

        // ★ 로컬 방 참가자 캐시 초기화
        var handler = ConnectionManager.getHandler();
        if (handler != null && handler.getPlayers() != null) {
            handler.getPlayers().clear();
            // 원하면 나 자신만 다시 넣어두는 것도 가능(구조에 따라 선택)
            // handler.getPlayers().put(handler.getMe().getPlayerId(), handler.getMe());
        }


        window.showScreen("main");
    }
}
