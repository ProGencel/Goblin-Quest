package com.kitswiki.gquest.hud.customActors;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kitswiki.gquest.systems.DayNightSystem;

public class ClockLabel extends Label {

    private final DayNightSystem dayNightSystem;
    private int lastDay = -1;
    private int lastMinute = -1;

    public ClockLabel(Skin skin, DayNightSystem dayNightSystem) {
        super("", skin);
        this.dayNightSystem = dayNightSystem;
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
       setText(String.format("Day %d\n%02d:%02d",day,h,m - (m%10)));
    }
}
