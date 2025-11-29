package shared.packet;

public class LoginRequestPacket extends Packet {
    private String nickname;

    public LoginRequestPacket(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
