package shared.packet;

public class LoginPacket extends Packet {
    private String nickname;

    public LoginPacket(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
