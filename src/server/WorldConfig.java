package server;

import static client.game.player.Unit.UNIT_SIZE_WIDTH;
import static client.game.player.Unit.UNIT_SIZE_HEIGHT;

public final class WorldConfig {

    private WorldConfig() {}

    public static final int WORLD_WIDTH = 1400;
    public static final int WORLD_HEIGHT = 800;

    public static final int OBSTACLE_X_MIN = 100;
    public static final int OBSTACLE_X_MAX = 600;

    public static final int OBSTACLE_Y_MIN = 100;
    public static final int OBSTACLE_Y_MAX = 650;

    private static final double SPAWN_P1_X = 85.0;
    private static final double SPAWN_P1_Y = 674.0;
    private static final double SPAWN_P2_X = 210.0;
    private static final double SPAWN_P2_Y = 782.0;

    // 기본 스폰(시작 지점)
//    public static final int SPAWN_X = (int) Math.round(((SPAWN_P1_X + SPAWN_P2_X) / 2.0) - (UNIT_SIZE_WIDTH / 2.0));
//    public static final int SPAWN_Y = (int) Math.round(((SPAWN_P1_Y + SPAWN_P2_Y) / 2.0) - (UNIT_SIZE_HEIGHT / 2.0));
    // Test 스폰(끝 지점)
    public static final int SPAWN_X = 1211;
    public static final int SPAWN_Y = 288;

    private static final int CP1_X1 = 369, CP1_Y1 = 27,  CP1_X2 = 494, CP1_Y2 = 139;
    // Test 스폰(체크포인트1 지점)
//    public static final int SPAWN_X = (int) Math.round(((CP1_X1 + CP1_X2) / 2.0) - (UNIT_SIZE_WIDTH / 2.0));
//    public static final int SPAWN_Y = (int) Math.round(((CP1_Y1 + CP1_Y2) / 2.0) - (UNIT_SIZE_HEIGHT / 2.0));
    private static final int CP2_X1 = 1041, CP2_Y1 = 686, CP2_X2 = 1166, CP2_Y2 = 799;
    // Test 스폰(체크포인트2 지점)
//    public static final int SPAWN_X = (int) Math.round(((CP2_X1 + CP2_X2) / 2.0) - (UNIT_SIZE_WIDTH / 2.0));
//    public static final int SPAWN_Y = (int) Math.round(((CP2_Y1 + CP2_Y2) / 2.0) - (UNIT_SIZE_HEIGHT / 2.0));

    private static final int END_X1 = 1162, END_Y1 = 60,  END_X2 = 1292, END_Y2 = 173;

    public static class Rect {
        private final int x, y, w, h;

        public Rect(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        public int getX() { return x; }
        public int getY() { return y; }
        public int getW() { return w; }
        public int getH() { return h; }
    }

    public static final Rect[] CHECKPOINTS = new Rect[] {
            new Rect(CP1_X1, CP1_Y1, CP1_X2 - CP1_X1, CP1_Y2 - CP1_Y1),

            new Rect(CP2_X1, CP2_Y1, CP2_X2 - CP2_X1, CP2_Y2 - CP2_Y1)
    };

    public static final Rect END_POINT =
            new Rect(END_X1, END_Y1, END_X2 - END_X1, END_Y2 - END_Y1);

    public static int respawnX(Rect r) {
        return clampX(r.getX() + Math.max(0, (r.getW() - UNIT_SIZE_WIDTH) / 2));
    }

    public static int respawnY(Rect r) {
        return clampY(r.getY() + Math.max(0, (r.getH() - UNIT_SIZE_HEIGHT) / 2));
    }

    public static int clampX(int x) {
        if (x < 0) return 0;
        int max = WORLD_WIDTH - UNIT_SIZE_WIDTH;
        if (x > max) return max;
        return x;
    }

    public static int clampY(int y) {
        if (y < 0) return 0;
        int max = WORLD_HEIGHT - UNIT_SIZE_HEIGHT;
        if (y > max) return max;
        return y;
    }
}
