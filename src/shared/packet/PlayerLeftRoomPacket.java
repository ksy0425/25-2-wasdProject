package shared.packet;

public class PlayerLeftRoomPacket extends Packet {
    private int playerId;

    public PlayerLeftRoomPacket(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() { return playerId; }
}
