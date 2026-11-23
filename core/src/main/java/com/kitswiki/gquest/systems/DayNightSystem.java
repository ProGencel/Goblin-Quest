package com.kitswiki.gquest.systems;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

public class DayNightSystem extends EntitySystem {

    private static final float DAY_LENGTH_SECONDS = 60f * 8f; // 1 oyun gunu


    private static final float[][] KEYS = {
        // Saat, R, G, B, Alpha
        { 0f,   0.18f, 0.24f, 0.48f, 1f}, // Gece
        { 5f,   0.22f, 0.28f, 0.52f, 1f},
        { 6f,   0.55f, 0.45f, 0.60f, 1f}, // Şafak öncesi
        { 7f,   1.00f, 0.75f, 0.58f, 1f}, // Şafak
        { 8.5f, 1.00f, 0.95f, 0.88f, 1f}, // Sabah
        {10f,   1.00f, 1.00f, 1.00f, 1f}, // Gün ışığı
        {16f,   1.00f, 1.00f, 1.00f, 1f},
        {17.5f, 1.00f, 0.84f, 0.67f, 1f}, // Öğleden sonra
        {19f,   1.00f, 0.65f, 0.40f, 1f}, // Gün batımı
        {20.5f, 0.55f, 0.45f, 0.65f, 1f}, // Alacakaranlık
        {22f,   0.25f, 0.28f, 0.50f, 1f},
        {24f,   0.18f, 0.24f, 0.48f, 1f}
    };


    private float hour;
    private int day;
    private final Color ambient = new Color(Color.WHITE);
    private final RayHandler rayHandler;

    public DayNightSystem(int day, float hour, RayHandler rayHandler) {

        this.day = day;
        this.hour = hour;
        this.rayHandler = rayHandler;

        updateAmbient();
    }

    @Override
    public void update(float deltaTime) {
        //deltaTime *= 10f;
        hour += deltaTime * (24f / DAY_LENGTH_SECONDS);
        if(hour >= 24f)
        {
            hour -= 24f;
            day++;
        }
        updateAmbient();
    }

    public void updateAmbinetLight(boolean isOutside)
    {
        if(isOutside)
        {
            rayHandler.setAmbientLight(ambient);
        }
        else
        {
            rayHandler.setAmbientLight(0.55f, 0.45f, 0.65f, 1f);
        }
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

    public float getHour() {
        return hour;
    }

    public int getDay() {
        return day;
    }
}
