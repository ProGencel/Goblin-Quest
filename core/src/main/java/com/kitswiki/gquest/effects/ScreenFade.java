package com.kitswiki.gquest.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class ScreenFade {

    private final float SPEED = 0.8f;

    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private float alpha;
    private int dir;

    public ScreenFade(boolean startBlack) {
        this.alpha = startBlack ? 1 : 0;
        this.dir = startBlack ? -1 : 0;
    }

    public void draw()
    {
        if(alpha <= 0)
        {
            return;
        }
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, alpha);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void update(float delta)
    {
        alpha = MathUtils.clamp(alpha + dir * SPEED * delta, 0f, 1f);
        if(dir == -1 && alpha == 0)
        {
            dir = 0;
        }
    }

    public void fadeOut()
    {
        dir = 1;
    }

    public boolean isFadedOut()
    {
        return alpha >= 1f;
    }

    public void dispose()
    {
        shapeRenderer.dispose();
    }
}
