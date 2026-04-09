package shared.packet;

import shared.model.PlayerState;

import java.util.List;

public class RoomInfoPacket extends Packet {
    private String roomTitle;
    private List<PlayerState> players;
    private int hostId;

    public RoomInfoPacket(String roomTitle, int hostId, List<PlayerState> playerIds) {
        this.roomTitle = roomTitle;
        this.hostId = hostId;
        this.players = playerIds;
    }

    public String getRoomTitle() { return roomTitle; }
    public int getHostId() { return hostId; }
    public List<PlayerState> getPlayers() { return players; }
}
