package shared.packet;

import shared.model.PlayerState;

import java.util.List;

public class GameStartRequestPacket extends Packet{
    private String title;
    private List<PlayerState> playerStateList;
    public GameStartRequestPacket(String title, List<PlayerState> playerStateList) {
        this.title = title;
        this.playerStateList = playerStateList;
    }

    public String getTitle() { return title; }
    public List<PlayerState> getPlayerStateList() {
        return playerStateList;
    }
}
