package shared.packet;

public class CreateRoomResponsePacket extends Packet {
    private boolean accepted;  // true=승인, false=거부
    private String reason;     // 거부 사유

    public CreateRoomResponsePacket(boolean accepted, String reason) {
        this.accepted = accepted;
        this.reason = reason;
    }

    public boolean isAccepted() { return accepted; }
    public String getReason() { return reason; }
}

