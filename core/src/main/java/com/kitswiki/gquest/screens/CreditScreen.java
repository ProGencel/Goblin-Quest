package com.kitswiki.gquest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

public class CreditScreen extends ScreenAdapter {

    private static final float SCROLL_SPEED = 45f;  // piksel/sn
    private static final float FAST_MULT    = 5f;   // basılı tutunca hız çarpanı
    private static final float FADE_IN      = 1.5f;
    private static final float HOLD         = 3f;   // son satırda bekleme
    private static final float FADE_OUT     = 2f;

    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Runnable onFinished;
    private final Viewport viewport = new FitViewport(640,360);

    // "#" ile başlayan = başlık, "" = boşluk. Son satır ekranda ortada kalır.
    private String[] lines;

    private static final String[] LINE_KEYS = new String[]{
        "#credits.title",
        "",
        "#credits.design",
        "credits.author",
        "",
        "#credits.programmer",
        "credits.author",
        "",
        "#credits.story",
        "credits.author",
        "",
        "#credits.asset",
        "credits.assetauthor",
        "",
        "#credits.thanks",
        "credits.supporters",
        "",
        "#credits.end",
        "#credits.end1"
    };

    private float scroll = 0f;
    private float elapsed = 0f;
    private float endTimer = 0f;
    private boolean finished = false;

    public CreditScreen( BitmapFont font, Runnable onFinished, I18NBundle bundle) {
        this.batch = new SpriteBatch();
        this.font = font;
        this.onFinished = onFinished;

        loadLines(bundle);
    }

    @Override
    public void render(float delta) {
        if (finished) return;
        elapsed += delta;

        float h = viewport.getWorldHeight();
        float spacing = font.getLineHeight() * 1.6f;
        float scrollMax = h / 2f + (lines.length - 1) * spacing; // son satır ortaya gelince dur

        boolean fast = Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE);
        scroll = Math.min(scroll + SCROLL_SPEED * (fast ? FAST_MULT : 1f) * delta, scrollMax);

        if (scroll >= scrollMax) endTimer += delta;
        if (endTimer >= HOLD + FADE_OUT) {
            finished = true;
            onFinished.run();
            return;
        }

        float master = MathUtils.clamp(elapsed / FADE_IN, 0f, 1f);
        if (endTimer > HOLD) master *= 1f - (endTimer - HOLD) / FADE_OUT;

        ScreenUtils.clear(0, 0, 0, 1);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        float width = viewport.getWorldWidth() * 0.8f;
        float x = (viewport.getWorldWidth() - width) / 2f;

        Color old = font.getColor().cpy();
        batch.begin();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.isEmpty()) continue;

            boolean header = line.startsWith("#");
            if (header) font.setColor(1f, 0.82f, 0.35f, master); // altın sarısı
            else        font.setColor(1f, 1f, 1f, master);

            String text = header ? line.substring(1).trim() : line;
            float y = MathUtils.round(scroll - i * spacing); // pixel-art'ta bulanıklık/titreme olmasın
            font.draw(batch, text, x, y, width, Align.center, false);
        }
        batch.end();
        font.setColor(old);
    }

    private void loadLines(I18NBundle bundle) {
        lines = new String[LINE_KEYS.length];

        for (int i = 0; i < LINE_KEYS.length; i++) {
            String key = LINE_KEYS[i];

            if (key.isEmpty()) {
                lines[i] = "";
                continue;
            }

            boolean header = key.startsWith("#");
            if (header) key = key.substring(1);

            String text = key.startsWith("credits.")
                ? bundle.get(key)
                : key;

            lines[i] = header ? "#" + text : text;
        }
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
    }
}
