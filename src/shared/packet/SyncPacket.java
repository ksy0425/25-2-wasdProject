package shared.packet;

public class SyncPacket extends Packet {

    private final int x;
    private final int y;

    public SyncPacket(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
