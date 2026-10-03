package com.kitswiki.gquest.map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class StaticBodyBuilder {

    private final Body body;
    private final World world;
    private final TiledMapReader mapReader;

    /** NEVER CALL THIS CLASS MORE THAN ONE TIME !!!!!!!!!!!**/
    public StaticBodyBuilder(World world, TiledMapReader mapReader) {
        this.world = world;
        this.mapReader = mapReader;
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        this.body = world.createBody(bdef);
    }

    public Body createStaticBody()
    {
        this.addRectsFromMap();
        this.addRectsFromObjects();
        return body;
    }

    private void addRectsFromObjects()
    {
        Array<TiledMapReader.MapShape> mapShapes = mapReader.getTileObjectShapes("objects");
        for(TiledMapReader.MapShape mapShape : mapShapes)
        {
            if(mapShape.type.equals(TiledMapReader.MapShape.Type.RECTANGLE))
            {
                Rectangle r = mapShape.bounds;

                PolygonShape shape = new PolygonShape();
                Vector2 center = new Vector2(r.x + r.width/2, r.y + r.height/2);
                shape.setAsBox(r.width / 2f, r.height / 2f,center,0f);
                this.attach(shape);
            }
        }
    }

    private void addRectsFromMap()
    {
        Array<Rectangle> rects = mapReader.getRects("collisions");

        for(Rectangle r : rects)
        {
            PolygonShape shape = new PolygonShape();
            Vector2 center = new Vector2(r.x + r.width/2, r.y + r.height/2);
            shape.setAsBox(r.width / 2f, r.height / 2f,center,0f);
            this.attach(shape);
        }
    }

    private void attach(Shape shape)
    {
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.friction = 0f;
        body.createFixture(fdef);
        shape.dispose();
    }

}
