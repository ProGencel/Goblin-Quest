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
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kitswiki.gquest.GameSession;
import com.kitswiki.gquest.managers.AudioManager;

public class EndingScreen extends ScreenAdapter {

    private static final float FADE_IN = 1f;
    private static final float FADE_OUT = 1f;
    private static final float LINE_TIME = 4f;

    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Runnable onFinished;
    private final Viewport viewport;
    private final GameSession gameSession;

    private final String[] lines = new String[5];

    private float time = 0f;
    private boolean finished = false;

    public EndingScreen(BitmapFont font, Runnable onFinished, I18NBundle bundle, GameSession gameSession) {
        this.batch = new SpriteBatch();
        this.font = font;
        this.onFinished = onFinished;
        this.viewport = new FitViewport(640, 360);
        this.gameSession = gameSession;

        for (int i = 0; i < lines.length; i++) {
            lines[i] = bundle.get("final." + (i + 1));
        }
    }

    @Override
    public void show() {
        gameSession.getAudioManager().playMusic(AudioManager.TYPE.MYSTERIOUS);
    }

    @Override
    public void render(float delta) {
        if (finished) return;
        time += delta;

        if (time >= lines.length * LINE_TIME) {
            finished = true;
            onFinished.run();
            return;
        }
        int index = (int) (time / LINE_TIME);
        float t = time - index * LINE_TIME;

        float alpha = MathUtils.clamp(
            Math.min(t / FADE_IN, (LINE_TIME - t) / FADE_OUT), 0f, 1f);

        ScreenUtils.clear(0, 0, 0, 1);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        float width = viewport.getWorldWidth() * 0.8f;
        float x = (viewport.getWorldWidth() - width) / 2f;
        float y = viewport.getWorldHeight() / 2f;

        Color old = font.getColor().cpy();
        font.setColor(1f, 1f, 1f, alpha);

        batch.begin();
        font.draw(batch, lines[index], x, y, width, Align.center, true);
        batch.end();

        font.setColor(old);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
