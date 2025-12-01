package shared.packet;

public class LoginResponsePacket extends Packet{
    private String nickname;
    private int playerId;

    private boolean accepted;
    private String reason;

    public LoginResponsePacket(String nickname, int playerId, boolean accepted, String reason) {
        this.nickname = nickname;
        this.playerId = playerId;

        this.accepted = accepted;
        this.reason = reason;
    }

    public String getNickname() {return this.nickname;}
    public int getPlayerId() {return this.playerId;}
    public boolean isAccepted() { return accepted; }
    public String getReason() { return reason; }
}
