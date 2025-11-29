package server;

import shared.packet.*;

public class PacketHandler {

    private final ClientHandler client;
    private final RoomManager roomManager;
    private final ServerWindow window;

    public PacketHandler(ClientHandler client, RoomManager roomManager, ServerWindow window) {
        this.client = client;
        this.roomManager = roomManager;
        this.window = window;
    }

    public void handle(Packet packet) {

        if (packet instanceof CreateRoomRequestPacket p) {
            handleCreateRoom(p);

        } else if (packet instanceof JoinRoomRequestPacket p) {
            handleJoinRoom(p);

        } else if (packet instanceof LeaveRoomPacket p) {
            handleLeaveRoom(p);

        } else {
            window.printDisplay("알 수 없는 패킷: " + packet.getClass().getSimpleName());
        }
    }

    private void handleCreateRoom(CreateRoomRequestPacket packet) {
        boolean ok = roomManager.createRoom(packet.getRoomTitle(), client);

        client.send(new CreateRoomResponsePacket(
                ok, ok ? "" : "이미 존재하는 방 제목"
        ));
    }

    private void handleJoinRoom(JoinRoomRequestPacket packet) {
        boolean ok = roomManager.joinRoom(packet.getRoomTitle(), client);

        client.send(new JoinRoomResponsePacket(
                ok, ok ? "" : "인원 초과 또는 방 없음"
        ));
    }

    private void handleLeaveRoom(LeaveRoomPacket packet) {
        roomManager.leaveRoom(client);

        roomManager.broadcastLobby(new PlayerLeftRoomPacket(client.getPlayerId()));
    }
}
