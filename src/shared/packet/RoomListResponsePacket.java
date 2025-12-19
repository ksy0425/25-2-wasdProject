package shared.packet;

import java.util.Map;
// 추가 구현
public class RoomListResponsePacket extends Packet {
    private final Map<String, Integer> rooms;

    public RoomListResponsePacket(Map<String, Integer> rooms) {
        this.rooms = rooms;
    }

    public Map<String, Integer> getRooms() {
        return rooms;
    }
}