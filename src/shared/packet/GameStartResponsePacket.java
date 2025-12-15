package shared.packet;

import java.util.Map;

public class GameStartResponsePacket extends Packet{
    private Map<Integer, String> playersKey;
    private boolean accepted;
    public GameStartResponsePacket(Map<Integer, String> playersKey, boolean accepted) {
        this.playersKey = playersKey;
        this.accepted = accepted;
    }

    public Map<Integer, String> getPlayersKey() {
        return playersKey;
    }

    public boolean isAccepted() {
        return accepted;
    }
}
