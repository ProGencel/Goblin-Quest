package com.kitswiki.gquest.generators;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

public class CloudGenerator {

    public static final int PIECE_SIZE = 16;

    private final Random random = new Random();
    private final TextureRegion[] byMask = new TextureRegion[16];
    private final Pixmap atlasPixmap;

    public CloudGenerator(AssetManager assetManager) {
        TextureAtlas atlas = assetManager.get("atlas/cooked/gquest.atlas");
        Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions("cloud");

        // Maske bitleri: TL=1, TR=2, BL=4, BR=8. Atlas index = 15 - mask
        for (int mask = 0; mask < 16; mask++) {
            byMask[mask] = regions.get(15 - mask);
        }

        // Atlas sayfasını CPU'ya al (bulut parçalarının hepsi aynı sayfada olmalı)
        TextureData data = regions.first().getTexture().getTextureData();
        if (!data.isPrepared()) data.prepare();
        atlasPixmap = data.consumePixmap();
    }

    public TextureRegion generateCloud() {
        int height = 5 + random.nextInt(4); // 5–8 hücre
        int minWidth = (int) Math.ceil(height * 1.25);
        int maxWidth = (int) Math.floor(height * 1.65);
        int width = minWidth + random.nextInt(maxWidth - minWidth + 1);
        boolean[][] filled = randomShape(width, height);

        Pixmap cloud = new Pixmap((width + 1) * PIECE_SIZE, (height + 1) * PIECE_SIZE, Pixmap.Format.RGBA8888);
        cloud.setBlending(Pixmap.Blending.None); // birebir kopyala

        for (int i = 0; i <= width; i++) {
            for (int j = 0; j <= height; j++) {
                int mask = 0;
                if (isFilled(filled, i - 1, j))     mask |= 1; // TL
                if (isFilled(filled, i,     j))     mask |= 2; // TR
                if (isFilled(filled, i - 1, j - 1)) mask |= 4; // BL
                if (isFilled(filled, i,     j - 1)) mask |= 8; // BR

                TextureRegion r = byMask[mask];
                // Pixmap'te y aşağı doğru, dünyada yukarı: satırı çevir
                cloud.drawPixmap(atlasPixmap,
                    i * PIECE_SIZE, (height - j) * PIECE_SIZE,
                    r.getRegionX(), r.getRegionY(), PIECE_SIZE, PIECE_SIZE);
            }
        }

        Texture tex = new Texture(cloud);
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        cloud.dispose();
        return new TextureRegion(tex);
    }

    private boolean[][] randomShape(int width, int height) {
        boolean[][] filled = new boolean[width][height];

        // Geniş gövde bütün parçaları ortada birbirine bağlıyor.
        addEllipse(filled, 0.50, 0.45, 0.50, 0.41);

        // En yüksek nokta her bulutta biraz sağa veya sola kayıyor.
        double peakX = 0.30 + 0.40 * random.nextDouble();
        double peakRadiusX = 0.24 + 0.06 * random.nextDouble();
        addEllipse(filled, peakX, 0.59, peakRadiusX, 0.41);

        // Sol ve sağ çıkıntının boyu ve yüksekliği birbirinden bağımsız.
        for (int side = 0; side < 2; side++) {
            double radiusX = 0.19 + 0.08 * random.nextDouble();
            double centerX = side == 0 ? radiusX : 1.0 - radiusX;
            double centerY = 0.40 + 0.18 * random.nextDouble();
            double radiusY = 0.30 + 0.08 * random.nextDouble();

            addEllipse(filled, centerX, centerY, radiusX, radiusY);
        }

        return filled;
    }

    private void addEllipse(boolean[][] filled, double centerX, double centerY,
                            double radiusX, double radiusY) {
        int width = filled.length;
        int height = filled[0].length;

        for (int x = 0; x < width; x++) {
            double dx = ((x + 0.5) / width - centerX) / radiusX;

            for (int y = 0; y < height; y++) {
                double dy = ((y + 0.5) / height - centerY) / radiusY;

                if (dx * dx + dy * dy <= 1.0) {
                    filled[x][y] = true;
                }
            }
        }
    }

    private boolean isFilled(boolean[][] g, int x, int y) {
        return x >= 0 && y >= 0 && x < g.length && y < g[0].length && g[x][y];
    }

    public void dispose() {
        atlasPixmap.dispose();
    }
}
