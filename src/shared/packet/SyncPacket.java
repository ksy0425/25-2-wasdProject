package shared.packet;

public class SyncPacket extends Packet {

    private final int x;
    private final int y;
    private final int Ox;
    private final int Oy;
    private int dir;
    private long elapsedMs;
    private boolean isFinished;

    public SyncPacket(int x, int y, int Ox, int Oy, int dir, long elapsedMs, boolean isFinished) {
        this.x = x;
        this.y = y;
        this.Ox = Ox;
        this.Oy = Oy;
        this.dir = dir;
        this.elapsedMs = elapsedMs;
        this.isFinished=isFinished;
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

    public long getElapsedMs() {
        return elapsedMs;
    }

    public boolean getIsFinished() {
        return isFinished;
    }
}
