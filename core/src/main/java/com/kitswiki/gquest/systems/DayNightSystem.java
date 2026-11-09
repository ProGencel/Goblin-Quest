package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

public class DayNightSystem extends EntitySystem {

    private static final float DAY_LENGTH_SECONDS = 60f * 8f; //1 oyun gunu

    private static final float[][] KEYS = {
        { 0f,   0.18f, 0.24f, 0.48f}, // gece (mavi gökyüzü, koyu)
        { 5f,   0.22f, 0.28f, 0.52f},
        { 6f,   0.55f, 0.45f, 0.60f}, // şafak öncesi, mor geçiş
        { 7f,   1.00f, 0.75f, 0.58f}, // şafak (sodium vapor)
        { 8.5f, 1.00f, 0.95f, 0.88f}, // sabah (halogen)
        {10f,   1.00f, 1.00f, 1.00f}, // gün ışığı
        {16f,   1.00f, 1.00f, 1.00f},
        {17.5f, 1.00f, 0.84f, 0.67f}, // öğleden sonra (tungsten)
        {19f,   1.00f, 0.65f, 0.40f}, // gün batımı
        {20.5f, 0.55f, 0.45f, 0.65f}, // alacakaranlık, mor geçiş
        {22f,   0.25f, 0.28f, 0.50f},
        {24f,   0.18f, 0.24f, 0.48f}, // 0f ile aynı, wrap'te sıçrama yok
    };

    private float hour = 8f;
    private int day = 1;
    private final Color ambient = new Color(Color.WHITE);
    private final Texture whitePixel;

    public DayNightSystem() {

        Pixmap pm = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        whitePixel = new Texture(pm);
        pm.dispose();
        updateAmbient();
    }

    @Override
    public void update(float deltaTime) {
        hour += deltaTime * (24f / DAY_LENGTH_SECONDS);
        System.out.println(hour);
        if(hour >= 24f)
        {
            hour -= 24f;
            day++;
        }
        updateAmbient();
    }

    private void updateAmbient()
    {
        for(int i = 0;i<KEYS.length-1;i++)
        {
            float[] a = KEYS[i];
            float[] b = KEYS[i+1];
            if(hour >= a[0] && hour < b[0])
            {
                float t = (hour - a[0]) / (b[0] - a[0]); //araligin yuzde kacindayiz
                ambient.set(MathUtils.lerp(a[1], b[1], t),
                    MathUtils.lerp(a[2], b[2], t),
                    MathUtils.lerp(a[3], b[3], t), 1f);
                return;
            }
        }
    }

    public void drawOverlay(SpriteBatch batch, Viewport screenViewport)
    {
        batch.setProjectionMatrix(screenViewport.getCamera().combined);
        batch.begin();
        batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO);
        batch.setColor(ambient);
        batch.draw(whitePixel, 0, 0, screenViewport.getWorldWidth(), screenViewport.getWorldHeight());
        batch.setColor(Color.WHITE);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.end();
    }

    public void dispose()
    {
        whitePixel.dispose();
    }

    public float getHour() {
        return hour;
    }

    public int getDay() {
        return day;
    }
}
