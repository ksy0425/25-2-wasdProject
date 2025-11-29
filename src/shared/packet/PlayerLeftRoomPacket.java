package shared.packet;

import server.packet.Packet;

public class PlayerLeftRoomPacket extends Packet {
    private int playerId;

    public PlayerLeftRoomPacket(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() { return playerId; }
}
