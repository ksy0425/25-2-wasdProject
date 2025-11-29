package shared.packet;

public class CreateRoomRequestPacket extends Packet {
    private String roomTitle;
    private int hostId;

    public CreateRoomRequestPacket(String roomTitle, int hostId) {
        this.roomTitle = roomTitle;
        this.hostId = hostId;
    }

    public String getRoomTitle() { return roomTitle; }
    public int getHostId() { return hostId; }
}
