
package com.kitswiki.gquest.debug;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import com.kitswiki.gquest.ai.enemy.EnemyAI;
import com.kitswiki.gquest.ai.enemy.EnemyState;
import com.kitswiki.gquest.ai.pathfinding.TileGraph;
import com.kitswiki.gquest.ai.pathfinding.TileNode;
import com.kitswiki.gquest.ai.skeleton.SkeletonAI;
import com.kitswiki.gquest.ai.skeleton.SkeletonState;
import com.kitswiki.gquest.components.EnemyAIComponent;
import com.kitswiki.gquest.components.SkeletonAIComponent;

public class AIDebugRenderer {

    private final ComponentMapper<EnemyAIComponent> aiMapper =
        ComponentMapper.getFor(EnemyAIComponent.class);

    private final ComponentMapper<SkeletonAIComponent> skeletonMapper =
        ComponentMapper.getFor(SkeletonAIComponent.class);

    private final ShapeRenderer sr = new ShapeRenderer();

    private final float ppm;
    private boolean isDebug = false;

    public AIDebugRenderer(float ppm) {
        this.ppm = ppm;
    }

    public void update(
        OrthographicCamera camera,
        ImmutableArray<Entity> aiEntities,
        ImmutableArray<Entity> skeletonEntities,
        TileGraph graph
    ) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            isDebug = !isDebug;
        }

        if (!isDebug) {
            return;
        }

        render(camera, aiEntities);
        renderGrid(camera, graph);
        renderConnections(camera, graph);
        renderPatrol(camera, aiEntities);
        renderSkeleton(camera, skeletonEntities);

        // Enemy paths
        for (int i = 0; i < aiEntities.size(); i++) {
            EnemyAI e = aiMapper.get(aiEntities.get(i)).enemyAI;
            renderPath(camera, graph, e.nav.getPath());
        }

        // Skeleton paths
        for (int i = 0; i < skeletonEntities.size(); i++) {
            SkeletonAI skeleton = skeletonMapper.get(skeletonEntities.get(i)).skeletonAI;
            renderPath(camera, graph, skeleton.nav.getPath());
        }
    }

    // --------------------------------------------------
    // ENEMY AI
    // --------------------------------------------------

    public void render(OrthographicCamera camera, ImmutableArray<Entity> entities) {

        sr.setProjectionMatrix(camera.combined);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Görüş konileri
        sr.begin(ShapeType.Filled);

        for (int i = 0; i < entities.size(); i++) {

            EnemyAI e = aiMapper.get(entities.get(i)).enemyAI;
            Vector2 p = e.steerable.getPosition();

            if (e.stateMachine.getCurrentState() == EnemyState.CHASE) {
                sr.setColor(1f, 0.2f, 0.2f, 0.18f);
            } else if (e.stateMachine.getCurrentState() == EnemyState.ATTACK) {
                sr.setColor(1f, 0.5f, 0f, 0.25f);
            } else {
                sr.setColor(0.3f, 0.6f, 1f, 0.15f);
            }

            float facingDeg = e.facing.angleDeg();

            sr.arc(
                p.x * ppm,
                p.y * ppm,
                e.detectRange * ppm,
                facingDeg - e.fovDegrees / 2f,
                e.fovDegrees
            );
        }

        sr.end();

        // Menzil daireleri ve görüş çizgileri
        sr.begin(ShapeType.Line);

        for (int i = 0; i < entities.size(); i++) {

            EnemyAI e = aiMapper.get(entities.get(i)).enemyAI;

            Vector2 p = e.steerable.getPosition();
            Vector2 t = e.target.getPosition();

            sr.setColor(1f, 1f, 1f, 0.35f);
            sr.circle(p.x * ppm, p.y * ppm, e.nearRange * ppm, 24);

            sr.setColor(1f, 0.5f, 0f, 0.6f);
            sr.circle(p.x * ppm, p.y * ppm, e.attackRange * ppm, 24);

            sr.setColor(0.6f, 0.6f, 0.6f, 0.4f);
            sr.circle(p.x * ppm, p.y * ppm, e.loseRange * ppm, 40);

            // Bakış yönü
            sr.setColor(Color.WHITE);

            sr.line(
                p.x * ppm,
                p.y * ppm,
                (p.x + e.facing.x) * ppm,
                (p.y + e.facing.y) * ppm
            );

            // Line of sight
            boolean los = e.hasLineOfSight();
            boolean sees = e.canSeeTarget();

            if (sees) {
                sr.setColor(Color.GREEN);
            } else if (los) {
                sr.setColor(Color.YELLOW);
            } else {
                sr.setColor(Color.RED);
            }

            sr.line(
                p.x * ppm,
                p.y * ppm,
                t.x * ppm,
                t.y * ppm
            );
        }

        sr.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void renderPatrol(
        OrthographicCamera camera,
        ImmutableArray<Entity> entities
    ) {
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeType.Line);

        for (int i = 0; i < entities.size(); i++) {

            EnemyAI e = aiMapper.get(entities.get(i)).enemyAI;

            for (int j = 0; j < e.nav.patrolPoints.size; j++) {

                Vector2 p = e.nav.patrolPoints.get(j);

                sr.setColor(
                    j == e.nav.getPatrolIndex()
                        ? Color.YELLOW
                        : Color.CYAN
                );

                sr.circle(p.x * ppm, p.y * ppm, 0.15f * ppm, 12);
            }
        }

        sr.end();
    }

    // --------------------------------------------------
    // SKELETON AI
    // --------------------------------------------------

    public void renderSkeleton(
        OrthographicCamera camera,
        ImmutableArray<Entity> entities
    ) {
        sr.setProjectionMatrix(camera.combined);

        sr.begin(ShapeType.Line);

        for (int i = 0; i < entities.size(); i++) {

            SkeletonAI skeleton = skeletonMapper.get(entities.get(i)).skeletonAI;

            Vector2 p = skeleton.getSteerableBody().getPosition();
            Vector2 destination = skeleton.cave;

            SkeletonState state =
                skeleton.getStateMachine().getCurrentState();

            // Destination
            sr.setColor(Color.YELLOW);
            sr.circle(
                destination.x * ppm,
                destination.y * ppm,
                0.15f * ppm,
                12
            );

            // Skeleton -> Destination
            sr.setColor(Color.CYAN);
            sr.line(
                p.x * ppm,
                p.y * ppm,
                destination.x * ppm,
                destination.y * ppm
            );

            // Skeleton state
            if (state == SkeletonState.GOTO_CAVE) {
                sr.setColor(Color.GREEN);
            } else {
                sr.setColor(Color.ORANGE);
            }

            sr.circle(
                p.x * ppm,
                p.y * ppm,
                0.2f * ppm,
                16
            );
        }

        sr.end();
    }

    // --------------------------------------------------
    // PATHFINDING
    // --------------------------------------------------

    public void renderGrid(OrthographicCamera camera, TileGraph g) {

        sr.setProjectionMatrix(camera.combined);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sr.begin(ShapeType.Filled);
        sr.setColor(1f, 0f, 0f, 0.25f);

        for (TileNode n : g.nodes) {

            if (n.walkable) continue;

            sr.rect(
                n.x * g.tileSize * ppm,
                n.y * g.tileSize * ppm,
                g.tileSize * ppm,
                g.tileSize * ppm
            );
        }

        sr.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void renderPath(
        OrthographicCamera camera,
        TileGraph g,
        DefaultGraphPath<TileNode> path
    ) {
        if (path.getCount() < 2) return;

        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeType.Line);
        sr.setColor(Color.CYAN);

        for (int i = 0; i < path.getCount() - 1; i++) {

            TileNode a = path.get(i);
            TileNode b = path.get(i + 1);

            sr.line(
                (a.x + 0.5f) * g.tileSize * ppm,
                (a.y + 0.5f) * g.tileSize * ppm,
                (b.x + 0.5f) * g.tileSize * ppm,
                (b.y + 0.5f) * g.tileSize * ppm
            );
        }

        sr.end();
    }

    public void renderConnections(OrthographicCamera camera, TileGraph g) {

        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeType.Line);
        sr.setColor(0.2f, 1f, 0.2f, 1f);

        for (TileNode n : g.nodes) {

            for (Connection<TileNode> c : n.connections) {

                TileNode m = c.getToNode();

                if (m.x != n.x && m.y != n.y) continue;
                if (m.x < n.x || m.y < n.y) continue;

                sr.line(
                    (n.x + 0.5f) * g.tileSize * ppm,
                    (n.y + 0.5f) * g.tileSize * ppm,
                    (m.x + 0.5f) * g.tileSize * ppm,
                    (m.y + 0.5f) * g.tileSize * ppm
                );
            }
        }

        sr.end();
    }

    public void dispose() {
        sr.dispose();
    }
}
