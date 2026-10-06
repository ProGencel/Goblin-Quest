package com.kitswiki.gquest.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.kitswiki.gquest.components.*;
import com.kitswiki.gquest.map.TiledMapReader;

import static com.kitswiki.gquest.utils.Constants.UNIT_SCALE;

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
        b.body = handleBodyForDynamicCircle(pos);

        AnimationComponent a = new AnimationComponent();
        a.layers.add("base");
        a.layers.add("mophair");
        a.layers.add("tools");

        TextureComponent tex = new TextureComponent();
        int PLAYER_FEET_PADDING = 25;
        int PLAYER_FRAME_HEIGHT = 64;
        tex.offsetY = (-PLAYER_FRAME_HEIGHT / 2f + PLAYER_FEET_PADDING) * UNIT_SCALE;

        e.add(t);
        e.add(b);
        e.add(new StateComponent());
        e.add(a);
        e.add(tex);
        e.add(new PlayerComponent());
        e.add(new MovementComponent());
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

        e.add(t);
        e.add(new StateComponent());
        e.add(a);
        e.add(tex);
        e.add(d);

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
