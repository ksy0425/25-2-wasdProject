package shared.packet;

import shared.model.PlayerState;

import java.util.List;

public class GameStartRequestPacket extends Packet{
    private List<PlayerState> playerStateList;
    public GameStartRequestPacket(List<PlayerState> playerStateList) {
        this.playerStateList = playerStateList;
    }
    public List<PlayerState> getPlayerStateList() {
        return playerStateList;
    }
}
