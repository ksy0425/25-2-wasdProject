package shared.packet;

public class MovePacket extends Packet {

    public static final int STOP  = 0;
    public static final int LEFT  = 1;
    public static final int RIGHT = 2;
    public static final int UP    = 3;
    public static final int DOWN  = 4;

    private final int playerId;
    private final int direction;

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
