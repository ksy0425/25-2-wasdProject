package shared.packet;

public class JoinRoomRequestPacket extends Packet {
    private String roomTitle;

    public JoinRoomRequestPacket(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    public String getRoomTitle() { return roomTitle; }
}