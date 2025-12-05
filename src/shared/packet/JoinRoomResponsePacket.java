package shared.packet;

public class JoinRoomResponsePacket extends Packet {
    private String roomTitle;
    private boolean accepted;
    private String reason;     // 인원 초과, 방 없음 등 사유
    private int hostId;

    public JoinRoomResponsePacket(String roomTitle, int hostId, boolean accepted, String reason) {
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
