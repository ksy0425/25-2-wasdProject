package shared.packet;

public class MovePacket extends Packet {

    // 방향 상수 (서버/클라 공통 사용)
    public static final int STOP  = 0;
    public static final int LEFT  = 1;
    public static final int RIGHT = 2;
    public static final int UP    = 3;
    public static final int DOWN  = 4;

    private final int playerId;   // 누가 보냈는지
    private final int direction;  // 위 상수 중 하나

    public MovePacket(int playerId, int direction) {
        this.playerId = playerId;
        this.direction = direction;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getDirection() {
        return direction;
    }
}
