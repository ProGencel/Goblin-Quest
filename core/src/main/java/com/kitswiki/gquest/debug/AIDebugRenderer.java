package com.kitswiki.gquest.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.kitswiki.gquest.ai.EnemyAI;
import com.kitswiki.gquest.ai.EnemyState;

public class AIDebugRenderer {

    private final ShapeRenderer sr = new ShapeRenderer();
    private final float ppm;   // kamera metre ile çalışıyorsa 1f, piksel ile çalışıyorsa PPM değerin

    public AIDebugRenderer(float ppm) {
        this.ppm = ppm;
    }

    public void render(OrthographicCamera camera, Array<EnemyAI> enemies) {
        sr.setProjectionMatrix(camera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // 1) Dolu şekiller: görüş konisi (yarı saydam)
        sr.begin(ShapeType.Filled);
        for (int i = 0; i < enemies.size; i++) {
            EnemyAI e = enemies.get(i);
            Vector2 p = e.steerable.getPosition();

            if (e.stateMachine.getCurrentState() == EnemyState.CHASE) sr.setColor(1f, 0.2f, 0.2f, 0.18f);
            else if (e.stateMachine.getCurrentState() == EnemyState.ATTACK) sr.setColor(1f, 0.5f, 0f, 0.25f);
            else sr.setColor(0.3f, 0.6f, 1f, 0.15f);

            float facingDeg = e.facing.angleDeg();
            sr.arc(p.x * ppm, p.y * ppm, e.detectRange * ppm,
                facingDeg - e.fovDegrees / 2f, e.fovDegrees);
        }
        sr.end();

        // 2) Çizgiler: menzil daireleri, bakış yönü, oyuncuya giden ray
        sr.begin(ShapeType.Line);
        for (int i = 0; i < enemies.size; i++) {
            EnemyAI e = enemies.get(i);
            Vector2 p = e.steerable.getPosition();
            Vector2 t = e.target.getPosition();

            sr.setColor(1f, 1f, 1f, 0.35f);
            sr.circle(p.x * ppm, p.y * ppm, e.nearRange * ppm, 24);    // arkadan fark etme mesafesi
            sr.setColor(1f, 0.5f, 0f, 0.6f);
            sr.circle(p.x * ppm, p.y * ppm, e.attackRange * ppm, 24);  // saldırı mesafesi
            sr.setColor(0.6f, 0.6f, 0.6f, 0.4f);
            sr.circle(p.x * ppm, p.y * ppm, e.loseRange * ppm, 40);    // vazgeçme mesafesi

            // baktığı yön
            sr.setColor(Color.WHITE);
            sr.line(p.x * ppm, p.y * ppm,
                (p.x + e.facing.x) * ppm, (p.y + e.facing.y) * ppm);

            // oyuncuya giden ray: rengi durumu anlatır
            boolean los = e.hasLineOfSight();
            boolean sees = e.canSeeTarget();
            if (sees)    sr.setColor(Color.GREEN);   // gerçekten görüyor
            else if (los) sr.setColor(Color.YELLOW); // duvar yok ama menzil/açı dışında
            else          sr.setColor(Color.RED);    // duvar engelliyor
            sr.line(p.x * ppm, p.y * ppm, t.x * ppm, t.y * ppm);
        }
        sr.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void dispose() {
        sr.dispose();
    }
}
