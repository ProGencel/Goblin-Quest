package com.kitswiki.gquest.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.kitswiki.gquest.components.*;
import com.kitswiki.gquest.map.TiledMapReader;

public class PortalFactory {

    private final TiledMapReader mapReader;
    private final Engine engine;
    private final World world;

    public PortalFactory(TiledMapReader mapReader, Engine engine, World world) {
        this.mapReader = mapReader;
        this.engine = engine;
        this.world = world;
    }

    public void buildPortals()
    {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;

        Body body = world.createBody(bdef);
        for(TiledMapReader.MapShape m : mapReader.getShapes("portals"))
        {
            Entity e = engine.createEntity();

            TransformComponent t = engine.createComponent(TransformComponent.class);
            t.position.x = m.bounds.x;
            t.position.y = m.bounds.y;

            BodyComponent b = engine.createComponent(BodyComponent.class);
            Fixture f = handleFixture(m,body);
            f.setSensor(true);
            f.setUserData(e);
            b.body = body;

            InteractComponent i = engine.createComponent(InteractComponent.class);

            PortalComponent p = engine.createComponent(PortalComponent.class);
            p.targetSpawn = m.getString("targetSpawn",null);
            p.targetMap = m.getString("targetMap",null);

            e.add(t);
            e.add(b);
            e.add(i);
            e.add(p);

            engine.addEntity(e);

        }
    }

    private Fixture handleFixture(TiledMapReader.MapShape mapShape, Body b)
    {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(mapShape.vertices);
        Fixture f = b.createFixture(polygonShape,1);
        polygonShape.dispose();

        return f;
    }

}
