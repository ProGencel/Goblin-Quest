package com.kitswiki.gquest.hud.customActors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ShadowLabel extends Label {
    private float offsetX = 1f, offsetY = -1f;   // pixel-art için 1-2 birim
    private float shadowAlpha = 0.7f;

    public ShadowLabel(CharSequence text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public ShadowLabel setShadow(float offsetX, float offsetY, float alpha) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.shadowAlpha = alpha;
        return this;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color c = getColor();
        float r = c.r, g = c.g, b = c.b, a = c.a;
        float x = getX(), y = getY();

        // 1) gölge
        c.set(0f, 0f, 0f, a * shadowAlpha);
        setPosition(x + offsetX, y + offsetY);
        super.draw(batch, parentAlpha);

        // 2) normal metin
        c.set(r, g, b, a);
        setPosition(x, y);
        super.draw(batch, parentAlpha);
    }
}
