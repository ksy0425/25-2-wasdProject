package shared.packet;

import java.util.Map;

public class GameStartResponsePacket {
    private Map<Integer, String> playersKey;
    public GameStartResponsePacket(Map<Integer, String> playersKey) {
        this.playersKey = playersKey;
    }

    public Map<Integer, String> getPlayersKey() {
        return playersKey;
    }
}
