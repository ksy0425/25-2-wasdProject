package server;

import shared.model.PlayerState;
import shared.packet.*;

import java.util.*;

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
        System.out.println("[SERVER] PacketHandler.handle: " + packet.getClass().getName());

        if (packet instanceof LoginRequestPacket p) {
            handleLogin(p);
        } else if (packet instanceof CreateRoomRequestPacket p) {
            handleCreateRoom(p);

        } else if (packet instanceof JoinRoomRequestPacket p) {
            handleJoinRoom(p);

        } else if (packet instanceof LeaveRoomPacket p) {
            handleLeaveRoom(p);

        } else if (packet instanceof  GameStartRequestPacket p) {
            handleGameStart(p);
        } else if (packet instanceof MovePacket p) {
            System.out.println("[SERVER] instanceof MovePacket 통과!");
            handleMove(p);
        } else {
            window.printDisplay("알 수 없는 패킷: " + packet.getClass().getSimpleName());
        }
    }

    private void handleLogin(LoginRequestPacket packet) {
        String nickname = packet.getNickname();
        if(roomManager.check(nickname)) {
            int playerId = roomManager.addClient(client, nickname);
            client.setNickname(nickname);
            client.setPlayerId(playerId);

            window.printDisplay("플레이어 접속: ID=" + playerId + ", 닉네임=" + nickname);

            client.send(new LoginResponsePacket(nickname, playerId, true, "Login Success"));
        }
        else {
            client.send(new LoginResponsePacket(nickname, 0, false, nickname + " is already exist"));
        }
    }

    private void handleCreateRoom(CreateRoomRequestPacket packet) {
        String title = packet.getRoomTitle();
        boolean ok = roomManager.createRoom(title, client);

        GameRoom room = roomManager.getRoom(title);
        int hostId = (ok && room != null) ? room.getHostId() : -1;

        client.send(new CreateRoomResponsePacket(
                title, hostId, ok, ok ? "" : "이미 존재하는 방 제목입니다."
        ));

        // ★ 방 생성 성공했으면 호스트 포함 전체 RoomInfo 갱신
        if (ok && room != null) {
            room.broadcastRoomInfo();
        }
    }

    private void handleJoinRoom(JoinRoomRequestPacket packet) {
        String title = packet.getRoomTitle();
        boolean ok = roomManager.joinRoom(title, client);

        client.send(new JoinRoomResponsePacket(
                packet.getRoomTitle(), roomManager.getRoom(title).getHostId(), ok, ok ? "" : "인원 초과 또는 방 없음"
        ));

        roomManager.getRoom(title).broadcastRoomInfo();
    }

    private void handleLeaveRoom(LeaveRoomPacket packet) {
        roomManager.leaveRoom(client);

        roomManager.broadcastLobby(new PlayerLeftRoomPacket(client.getPlayerId()));
    }

    private void handleGameStart(GameStartRequestPacket packet) {
        List<PlayerState> players = packet.getPlayerStateList();

        List<String> keys = new ArrayList<>();
        keys.add("w");
        keys.add("a");
        keys.add("s");
        keys.add("d");

        Collections.shuffle(keys);

        Map<Integer, String> playersKey = new HashMap<>();

        for (int i = 0; i < players.size() && i < keys.size(); i++) {
            PlayerState ps = players.get(i);
            int playerId = ps.getPlayerId();
            String key = keys.get(i);

            playersKey.put(playerId, key);
        }
        GameRoom room = roomManager.getRoom(packet.getTitle());
        if (room != null) {
            room.broadcast(new GameStartResponsePacket(playersKey));
            room.startGameLoop();   // ★ 여기서 공유 유닛 게임 루프 시작
        }
    }

    private void handleMove(MovePacket packet) {
//        System.out.println("!!!![SERVER] MovePacket from playerId=" + packet.getPlayerId()
//                + ", dir=" + packet.getDirection());
        window.printDisplay("!!!![SERVER] MovePacket from playerId=" + packet.getPlayerId()
                + ", dir=" + packet.getDirection());
        GameRoom room = client.getCurrentRoom();
        if (room == null) return;

        // 단순히 방에 위임하면 됨 (Last Input Wins 논리는 GameRoom에 있음)
        System.out.println("====" + packet.getPlayerId() +", " + packet.getDirection()+"====");
        room.handleMove(packet);
    }
}
