package com.kitswiki.gquest.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.kitswiki.gquest.GameSession;
import com.kitswiki.gquest.components.CloudComponent;
import com.kitswiki.gquest.components.TransformComponent;
import com.kitswiki.gquest.generators.CloudGenerator;
import com.kitswiki.gquest.map.TiledMapReader;

import java.util.Random;

public class CloudFactory {

    private final Engine engine;
    private final GameSession gameSession;
    private final TextureAtlas atlas;
    private final CloudGenerator generator;
    private final Array<TiledMapReader.MapShape> recs;
    private final String currMap;

    public CloudFactory(Engine engine, GameSession gameSession, TiledMapReader mapReader,String currMap) {
        this.engine = engine;
        this.gameSession = gameSession;
        this.atlas = gameSession.getAssetManager().get("atlas/cooked/gquest.atlas");
        this.generator = new CloudGenerator(gameSession.getAssetManager());
        this.currMap = currMap;

        this.recs = mapReader.getShapes("clouds");
    }

    public void createCloud()
    {
        if("cave".equals(currMap))
        {
            return;
        }

        Random random = new Random();
        Entity e = engine.createEntity();

        CloudComponent c = engine.createComponent(CloudComponent.class);
        c.region = generator.generateCloud();

        TransformComponent t = engine.createComponent(TransformComponent.class);
        t.position = getPositionRandom(c,random);
        c.speed = MathUtils.random(20f,80f);
        c.speed *= c.direction;

        e.add(t);
        e.add(c);

        engine.addEntity(e);
    }

    public Runnable getCreateCloud()
    {
        return this::createCloud;
    }

    private Vector2 getPositionRandom(CloudComponent c, Random random)
    {
        TiledMapReader.MapShape rec = recs.random();
        Rectangle rect = rec.bounds;
        if(rect.x < 0)
        {
            c.direction = 1; // right direction
        }
        else
        {
            c.direction = -1;
        }

        float x = random.nextFloat(rect.x, rect.x + rect.width);
        float y = random.nextFloat(rect.y, rect.y + rect.height);
        return new Vector2(x,y);
    }

}
