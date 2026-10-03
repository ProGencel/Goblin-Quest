package com.kitswiki.gquest.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.kitswiki.gquest.components.*;
import com.kitswiki.gquest.map.TiledMapReader;

public class EntityFactory {

    private final World world;
    private final TiledMapReader mapReader;

    public EntityFactory(World world, TiledMapReader mapReader) {
        this.world = world;
        this.mapReader = mapReader;
    }

    public Entity createPlayer(Vector2 pos)
    {
        Entity e = new Entity();

        TransformComponent t = new TransformComponent();
        t.position.set(pos);

        BodyComponent b = new BodyComponent();
        b.body = handleBodyForPlayer(pos);

        AnimationComponent a = new AnimationComponent();
        a.layers.add("base");
        a.layers.add("mophair");
        a.layers.add("tools");

        e.add(t);
        e.add(b);
        e.add(new StateComponent());
        e.add(a);
        e.add(new TextureComponent());
        e.add(new PlayerComponent());
        e.add(new MovementComponent());
        return e;
    }

    public Entity createStaticObjects(TiledMapReader.MapTileObject mapObject)
    {
        Entity e = new Entity();

        TransformComponent t = new TransformComponent();
        t.position.x = mapObject.x + mapObject.width/2;
        t.position.y = mapObject.y + mapObject.height/2;

        TextureComponent tex = new TextureComponent();
        tex.regions.add(mapObject.region);

        e.add(t);
        e.add(tex);

        return e;
    }

    private Body handleBodyForPlayer(Vector2 pos)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos);
        bodyDef.fixedRotation = true;

        Body b = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(0.1f);
        shape.setPosition(new Vector2(0,-0.3f));
        b.createFixture(shape,1);
        shape.dispose();

        return b;
    }

}
