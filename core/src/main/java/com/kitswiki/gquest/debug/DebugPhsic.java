package com.kitswiki.gquest.debug;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.kitswiki.gquest.components.TextureComponent;
import com.kitswiki.gquest.components.TransformComponent;
import com.kitswiki.gquest.systems.RenderSystem;

public class DebugPhsic {

    private final ComponentMapper<TransformComponent> tsc = ComponentMapper.getFor(TransformComponent.class);

    private final ShapeRenderer shape;
    private final OrthographicCamera camera;
    private final World world;
    private final Box2DDebugRenderer debugRenderer;
    private final Engine engine;
    private final RenderSystem.YComparator debugComparator;

    private boolean isYSortDebug = false;
    private boolean isHitboxDebug = false;

    public DebugPhsic(OrthographicCamera camera, Engine engine, World world) {
        this.camera = camera;
        this.engine = engine;
        this.world = world;
        this.debugRenderer = new Box2DDebugRenderer();
        this.shape = new ShapeRenderer();
        this.debugComparator = new RenderSystem.YComparator();
    }

    public void update()
    {
        if(Gdx.input.isKeyJustPressed(Input.Keys.F4))
        {
            isHitboxDebug = !isHitboxDebug;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.F5))
        {
            isYSortDebug = !isYSortDebug;
        }

        if(isHitboxDebug)
        {
            debugRenderer.render(world,camera.combined);
        }
        if(isYSortDebug)
        {
            debugDrawSortLines();
        }
    }

    private void debugDrawSortLines() {
        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Line);
        for (Entity e :  engine.getEntitiesFor(
            Family.all(TransformComponent.class, TextureComponent.class).get())) {
            TransformComponent t = tsc.get(e);
            float y = debugComparator.sortY(e);
            shape.setColor(Color.RED);
            shape.line(t.position.x - 0.5f, y, t.position.x + 0.5f, y);
            shape.setColor(Color.GREEN);
            shape.circle(t.position.x, t.position.y, 0.05f, 8);
        }
        shape.end();
    }

}
