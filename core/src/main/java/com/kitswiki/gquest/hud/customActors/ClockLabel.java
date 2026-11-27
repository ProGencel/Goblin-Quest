package com.kitswiki.gquest.hud.customActors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;
import com.kitswiki.gquest.GameSession;
import com.kitswiki.gquest.systems.DayNightSystem;

public class ClockLabel extends Label {

    private final DayNightSystem dayNightSystem;
    private int lastDay = -1;
    private int lastMinute = -1;
    private final I18NBundle bundle;
    private final Color OUTLINE_COLOR;
    private final Color originalColor = new Color();

    public ClockLabel(Skin skin, DayNightSystem dayNightSystem, I18NBundle bundle, GameSession gameSession) {
        super("", skin);
        this.dayNightSystem = dayNightSystem;
        this.bundle = bundle;
        OUTLINE_COLOR = skin.getColor("outline");
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        float hour = dayNightSystem.getHour();

        int h = (int) hour;
        int m = (int) ((hour - h) * 60f);
        int day = dayNightSystem.getDay();

       int minuteOfDay = h * 60 + m;
       if(minuteOfDay == lastMinute && day == lastDay)
       {
           return;
       }
       lastMinute = minuteOfDay;
       lastDay = day;
       setText(String.format(bundle.get("day")+" %d\n%02d:%02d",day,h,m - (m%10)));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float x = getX();
        float y = getY();

        originalColor.set(getColor());

        setColor(OUTLINE_COLOR);
        getColor().a = originalColor.a;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                setPosition(x + dx, y + dy);

                super.draw(batch, parentAlpha);
            }
        }

        setPosition(x, y);
        setColor(originalColor);

        super.draw(batch, parentAlpha);
    }
}
