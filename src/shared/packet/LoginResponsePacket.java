package shared.packet;

public class LoginResponsePacket extends Packet{
    private String nickname;
    private int playerId;

    public LoginResponsePacket(String nickname, int playerId) {
        this.nickname = nickname;
        this.playerId = playerId;
    }

    public String getNickname() {return this.nickname;}
    public int getPlayerId() {return this.playerId;}
}
