package shared.packet;

public class CreateRoomRequestPacket extends Packet {
    private String roomTitle;

    public CreateRoomRequestPacket(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    public String getRoomTitle() { return roomTitle; }
}
