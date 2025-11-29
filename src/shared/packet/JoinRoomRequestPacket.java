package shared.packet;

public class JoinRoomRequestPacket extends Packet {
    private String roomTitle;
    //private int playerId;

    public JoinRoomRequestPacket(String roomTitle) {
        this.roomTitle = roomTitle;
        //this.playerId = playerId;
    }

    public String getRoomTitle() { return roomTitle; }
    //public int getPlayerId() { return playerId; }
}