package shared.packet;

public class LeaveRoomPacket extends Packet {
    private int playerId;

    public LeaveRoomPacket(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() { return playerId; }
}