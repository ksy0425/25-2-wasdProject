package server;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import static client.game.player.Unit.*;

public final class CollisionMask {

    private static final CollisionMask INSTANCE = new CollisionMask();

    private static final int SAFE_LUMINANCE = 250;

    private final BufferedImage mask;

    private CollisionMask() {
        this.mask = loadCollisionMask();
    }

    public static CollisionMask getInstance() {
        return INSTANCE;
    }

    public boolean hitUnit(int ux, int uy) {
        int mw = mask.getWidth();
        int mh = mask.getHeight();

        if (ux < 0 || uy < 0 || ux + UNIT_SIZE_WIDTH > mw || uy + UNIT_SIZE_HEIGHT > mh) {
            return true;
        }

        for (int y = uy; y < uy + UNIT_SIZE_HEIGHT; y++) {
            for (int x = ux; x < ux + UNIT_SIZE_WIDTH; x++) {
                if (isDangerPixel(mask.getRGB(x, y))) return true;
            }
        }
        return false;
    }

    private static boolean isDangerPixel(int argb) { // 외부 참조
        int a = (argb >>> 24) & 0xFF;
        if (a == 0) return false;

        int r = (argb >>> 16) & 0xFF;
        int g = (argb >>> 8) & 0xFF;
        int b = (argb) & 0xFF;

        int lum = (r + g + b) / 3;
        return lum < SAFE_LUMINANCE;
    }

    private static BufferedImage loadCollisionMask() { // 외부 참조
        String[] candidates = {"/CollusionMask.png", "/collusionMask.png", "/CollisionMask.png"};
        for (String path : candidates) {
            try (InputStream is = CollisionMask.class.getResourceAsStream(path)) {
                if (is == null) continue;
                return ImageIO.read(is);
            } catch (Exception ignored) {}
        }
        throw new RuntimeException("충돌 마스크 로딩 실패: resources에 CollusionMask.png가 없음");
    }
}
