package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kitswiki.gquest.components.BodyComponent;
import com.kitswiki.gquest.components.PlayerComponent;
import com.kitswiki.gquest.map.TiledMapReader;

public class CameraSystem extends IteratingSystem {

    private final ComponentMapper<BodyComponent> b = ComponentMapper.getFor(BodyComponent.class);

    private final OrthographicCamera camera;
    private final TiledMapReader mapReader;
    private final Viewport viewport;

    public CameraSystem(OrthographicCamera camera, TiledMapReader mapReader, Viewport viewport) {
        super(Family.all(PlayerComponent.class).get());

        this.camera = camera;
        this.mapReader = mapReader;
        this.viewport = viewport;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BodyComponent b = this.b.get(entity);

        float camX = b.body.getPosition().x;
        float camY = b.body.getPosition().y;

        float mapWidth = mapReader.getWidthWorld();
        float mapHeight = mapReader.getHeightWorld();

        camX = MathUtils.clamp(camX,0+viewport.getWorldWidth()/2,mapWidth-viewport.getWorldWidth()/2);
        camY = MathUtils.clamp(camY,0+viewport.getWorldHeight()/2,mapHeight-viewport.getWorldHeight()/2);

        camera.position.set(camX,camY,0);
        camera.update();

    }
}
