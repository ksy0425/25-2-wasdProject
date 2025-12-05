package shared.packet;

public class CreateRoomResponsePacket extends Packet {
    private String roomTitle;
    private boolean accepted;  // true=승인, false=거부
    private String reason;     // 거부 사유
    private int hostId;

    public CreateRoomResponsePacket(String roomTitle, int hostId, boolean accepted, String reason) {
        this.roomTitle = roomTitle;
        this.hostId = hostId;
        this.accepted = accepted;
        this.reason = reason;
    }

    public String getRoomTitle() { return roomTitle; }
    public int getHostId() { return hostId; }
    public boolean isAccepted() { return accepted; }
    public String getReason() { return reason; }
}

