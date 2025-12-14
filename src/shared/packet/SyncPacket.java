package shared.packet;

public class SyncPacket extends Packet {

    private final int x;
    private final int y;
    private final int Ox;
    private final int Oy;
    private int dir;

    public SyncPacket(int x, int y, int Ox, int Oy, int dir) {
        this.x = x;
        this.y = y;
        this.Ox = Ox;
        this.Oy = Oy;
        this.dir = dir;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getOx() {
        return Ox;
    }

    public int getOy() {
        return Oy;
    }

    public int getDir() {
        return dir;
    }
}
