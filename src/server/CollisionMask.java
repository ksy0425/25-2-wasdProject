package server;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import static client.game.player.Unit.*;

public final class CollisionMask {

    private static final CollisionMask INSTANCE = new CollisionMask();

    // ✅ 기존과 동일: 거의 흰색(>=250)만 안전
    private static final int SAFE_LUMINANCE = 250;

    private final BufferedImage mask;

    private CollisionMask() {
        this.mask = loadCollisionMask();
    }

    public static CollisionMask getInstance() {
        return INSTANCE;
    }

    // ✅ 유닛 좌상단 (ux,uy) 기준으로 UNIT_SIZE_WIDTH x UNIT_SIZE_HEIGHT 영역이
    // 마스크의 검정(어두운) 영역과 겹치면 true.
    public boolean hitUnit(int ux, int uy) {
        int mw = mask.getWidth();
        int mh = mask.getHeight();

        // 마스크 밖으로 나가면 충돌로 처리(기존과 동일)
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

    private static boolean isDangerPixel(int argb) {
        int a = (argb >>> 24) & 0xFF;
        if (a == 0) return false;

        int r = (argb >>> 16) & 0xFF;
        int g = (argb >>> 8) & 0xFF;
        int b = (argb) & 0xFF;

        int lum = (r + g + b) / 3;
        return lum < SAFE_LUMINANCE;
    }

    private static BufferedImage loadCollisionMask() {
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
