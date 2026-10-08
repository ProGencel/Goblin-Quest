package com.kitswiki.gquest.map;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class StaticBodyBuilder {

    private final Body body;
    private final Engine engine;
    private final TiledMapReader mapReader;
    private final World world;

    /** NEVER CALL THIS CLASS MORE THAN ONE TIME !!!!!!!!!!!**/
    public StaticBodyBuilder(World world, TiledMapReader mapReader, Engine engine) {
        this.world = world;
        this.mapReader = mapReader;
        this.engine = engine;
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        this.body = world.createBody(bdef);
    }

    public Body createStaticBody() {
        addShapesFromLayer("collisions", false);
        addShapesFromLayer("objects", true);

        return body;
    }

    public void addShapesFromLayer(String layerName, boolean includeTileObjects) {
        if (!mapReader.hasLayer(layerName)) return;

        Array<TiledMapReader.MapShape> shapes = mapReader.getShapes(layerName);
        if (includeTileObjects) {
            shapes.addAll(mapReader.getTileObjectShapes(layerName));
        }

        for (TiledMapReader.MapShape mapShape : shapes) {
            String shapeType = mapShape.getString("type", "");
            if (!shapeType.isEmpty() && !"static".equals(shapeType)) continue;

            switch (mapShape.type) {
                case POLYGON:
                    if (mapShape.vertices != null && mapShape.vertices.length >= 6) {
                        PolygonShape shape = new PolygonShape();
                        shape.set(mapShape.vertices);
                        attach(shape);
                    }
                    break;

                case RECTANGLE:
                    Rectangle r = mapShape.bounds;
                    PolygonShape box = new PolygonShape();
                    box.setAsBox(r.width / 2f, r.height / 2f,
                        new Vector2(r.x + r.width / 2f, r.y + r.height / 2f), 0f);
                    attach(box);
                    break;

                case POLYLINE:
                    if (mapShape.vertices != null && mapShape.vertices.length >= 4) {
                        ChainShape chain = new ChainShape();
                        chain.createChain(mapShape.vertices);
                        attach(chain);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private Fixture attach(Shape shape)
    {
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.friction = 0f;
        Fixture fixture = body.createFixture(fdef);
        shape.dispose();

        return fixture;
    }
}
