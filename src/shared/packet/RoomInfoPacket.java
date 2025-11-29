package shared.packet;

import server.packet.Packet;

import java.util.List;

public class RoomInfoPacket extends Packet {
    private String roomTitle;
    private List<Integer> playerIds;
    private int hostId;

    public RoomInfoPacket(String roomTitle, int hostId, List<Integer> playerIds) {
        this.roomTitle = roomTitle;
        this.hostId = hostId;
        this.playerIds = playerIds;
    }

    public String getRoomTitle() { return roomTitle; }
    public int getHostId() { return hostId; }
    public List<Integer> getPlayerIds() { return playerIds; }
}
