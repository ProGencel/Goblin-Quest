package com.kitswiki.gquest.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class EndingScreen extends ScreenAdapter {

    private static final float FADE_IN = 2f;
    private static final float LINE_GAP = 3.5f;
    private static final float HOLD = 3f;
    private static final float FADE_OUT = 2f;

    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Runnable onFinished;
    private final Viewport viewport;
    private final I18NBundle bundle;

    private final String[] lines = new String[5];

    private float time = 0f;
    private boolean finished = false;

    public EndingScreen(BitmapFont font, Runnable onFinished, I18NBundle bundle) {
        this.batch = new SpriteBatch();
        this.font = font;
        this.onFinished = onFinished;
        this.viewport = new FitViewport(640,360);
        this.bundle = bundle;

        lines[0] = bundle.get("final.1");
        lines[1] = bundle.get("final.2");
        lines[2] = bundle.get("final.3");
        lines[3] = bundle.get("final.4");
        lines[4] = bundle.get("final.5");
    }

    @Override
    public void render(float delta) {
        time += delta;

        float showEnd = (lines.length - 1) * LINE_GAP + FADE_IN + HOLD;
        float master = 1f;
        if(time > showEnd)
        {
            master = 1f - (time - showEnd) / FADE_OUT;
            if(master <= 0f && !finished)
            {
                finished = true;
                onFinished.run();
                return;
            }
        }
        master = MathUtils.clamp(master,0f,1f);

        ScreenUtils.clear(0, 0,0,1);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        float spacing = font.getLineHeight() * 2f;
        float width = viewport.getWorldWidth() * 0.8f;
        float x = (viewport.getWorldWidth() - width) / 2f;
        float y = viewport.getWorldHeight() / 2f + lines.length * spacing / 2f;

        Color old = font.getColor().cpy();
        batch.begin();
        for(int i = 0;i<lines.length;i++)
        {
            float a = MathUtils.clamp((time - 1 * LINE_GAP) / FADE_IN, 0f, 1f) * master;
            font.setColor(1f,1f,1f,a);
            font.draw(batch, lines[i],x,y - i*spacing,width, Align.center,false);
        }
        batch.end();
        font.setColor(old);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height,true);
    }
}
