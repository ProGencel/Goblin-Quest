package com.kitswiki.gquest.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.kitswiki.gquest.ai.EnemyAI;
import com.kitswiki.gquest.ai.SteerableBody;
import com.kitswiki.gquest.components.*;
import com.kitswiki.gquest.map.TiledMapReader;

import static com.kitswiki.gquest.utils.Constants.UNIT_SCALE;

public class EntityFactory {

    private final World world;
    private final TiledMapReader mapReader;
    private final Engine engine;

    public EntityFactory(World world, TiledMapReader mapReader, Engine engine) {
        this.world = world;
        this.mapReader = mapReader;
        this.engine = engine;
    }

    public Entity createPlayer(Vector2 pos)
    {
        Entity e = new Entity();

        TransformComponent t = new TransformComponent();
        t.position.set(pos);

        BodyComponent b = new BodyComponent();
        b.body = handleBodyForDynamicCircle(pos);
        b.body.setUserData(e);

        AnimationComponent a = new AnimationComponent();
        a.layers.add("base");
        a.layers.add("mophair");
        a.layers.add("tools");

        TextureComponent tex = new TextureComponent();
        int PLAYER_FEET_PADDING = 25;
        int PLAYER_FRAME_HEIGHT = 64;
        tex.offsetY = (-PLAYER_FRAME_HEIGHT / 2f + PLAYER_FEET_PADDING) * UNIT_SCALE;

        SteerableComponent s = engine.createComponent(SteerableComponent.class);
        s.steerableBody = new SteerableBody(b.body,0.5f);

        e.add(t);
        e.add(b);
        e.add(new StateComponent());
        e.add(a);
        e.add(tex);
        e.add(new PlayerComponent());
        e.add(new MovementComponent());
        e.add(new ContactComponent());
        e.add(s);

        return e;
    }

    public Entity createBadGoblin(TiledMapReader.MapTileObject mapObject,float OFFSET_Y, int FRAME_H, Entity player)
    {
        Entity e = engine.createEntity();

        TransformComponent t = engine.createComponent(TransformComponent.class);
        t.position.x = mapObject.x + mapObject.width / 2f;
        t.position.y = mapObject.y + mapObject.height / 2f;

        BodyComponent b = engine.createComponent(BodyComponent.class);
        b.body = handleBodyForDynamicRect(mapObject);

        AIComponent ai = engine.createComponent(AIComponent.class);
        SteerableBody steerable = new SteerableBody(b.body, 0.5f);
        steerable.setMaxLinearSpeed(1.5f);
        steerable.setMaxLinearAcceleration(8f);

        ai.enemyAI = new EnemyAI(steerable, player.getComponent(SteerableComponent.class).steerableBody,world);

        e.add(t);
        e.add(b);
        e.add(ai);

        return e;
    }

    public Entity createGoblin(TiledMapReader.MapTileObject mapObject, float offSetY, int frameH)
    {
        Entity e = new Entity();


        TransformComponent t = new TransformComponent();
        t.position.x = mapObject.x + mapObject.width/2;
        t.position.y = mapObject.y + mapObject.height/2;

        AnimationComponent a = new AnimationComponent();
        a.layers.add("spr");

        TextureComponent tex = new TextureComponent();
        tex.offsetY = (-frameH / 2f + offSetY) * UNIT_SCALE;
        tex.flipped = mapObject.getBool("isFlipped",true);

        DialogComponent d = new DialogComponent();
        d.dialogId = "goblin_intro";

        BodyComponent b = new BodyComponent();
        b.body = handleBodyForStaticRect(mapObject);
        b.body.setUserData(e);


        e.add(t);
        e.add(new StateComponent());
        e.add(a);
        e.add(tex);
        e.add(d);
        e.add(b);
        e.add(new InteractComponent());

        return e;
    }

    public Entity createStaticObjects(TiledMapReader.MapTileObject mapObject, float offsetY, int frameH)
    {
        Entity e = new Entity();

        TransformComponent t = new TransformComponent();
        t.position.x = mapObject.x + mapObject.width/2;
        t.position.y = mapObject.y + mapObject.height/2;

        TextureComponent tex = new TextureComponent();
        tex.regions.add(mapObject.region);
        tex.offsetY = ( -frameH / 2f + offsetY ) * UNIT_SCALE;
        e.add(t);
        e.add(tex);

        return e;
    }

    private Body handleBodyForDynamicRect(TiledMapReader.MapTileObject mapObject)
    {
        Array<TiledMapReader.MapShape> shapes = mapObject.shapes;

        float halfW = mapObject.width / 2f;
        float halfH = mapObject.height / 2f;

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.x = mapObject.x + halfW;
        bdef.position.y = mapObject.y + halfH;
        bdef.fixedRotation = true;

        Body b = world.createBody(bdef);

        for(TiledMapReader.MapShape m : shapes)
        {
            Rectangle r = m.bounds;
            float bodyX = mapObject.x + halfW;
            float bodyY = mapObject.y + halfH;

            PolygonShape shape = new PolygonShape();
            Vector2 center = new Vector2(
                r.x + r.width / 2f - bodyX,
                r.y + r.height / 2f - bodyY);
            shape.setAsBox(r.width / 2f, r.height / 2f,center,0f);
            b.createFixture(shape, 1f);
            shape.dispose();
        }
        return b;
    }

    private Body handleBodyForStaticRect(TiledMapReader.MapTileObject mapTileObject)
    {
        Array<TiledMapReader.MapShape> shapes = mapTileObject.shapes;

        float halfW = mapTileObject.width / 2f;
        float halfH = mapTileObject.height / 2f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(mapTileObject.x + halfW, mapTileObject.y + halfH);
        bodyDef.fixedRotation = true;

        Body b = world.createBody(bodyDef);

        for(TiledMapReader.MapShape m : shapes)
        {
            Rectangle r = m.bounds;
            float bodyX = mapTileObject.x + halfW;
            float bodyY = mapTileObject.y + halfH;

            PolygonShape shape = new PolygonShape();
            Vector2 center = new Vector2(
                r.x + r.width / 2f - bodyX,
                r.y + r.height / 2f - bodyY);
            shape.setAsBox(r.width / 2f, r.height / 2f,center,0f);
            b.createFixture(shape, 1f);
            shape.dispose();
        }

        return b;
    }


    private Body handleBodyForDynamicCircle(Vector2 pos)
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

        CircleShape sensorShape = new CircleShape();
        sensorShape.setRadius(1f);
        FixtureDef fdef = new FixtureDef();
        fdef.isSensor = true;
        fdef.shape = sensorShape;
        b.createFixture(fdef);
        sensorShape.dispose();


        return b;
    }

}
