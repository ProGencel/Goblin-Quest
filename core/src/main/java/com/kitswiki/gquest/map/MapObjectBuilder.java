package com.kitswiki.gquest.map;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.kitswiki.gquest.components.RoofComponent;
import com.kitswiki.gquest.factories.EntityFactory;
import com.kitswiki.gquest.systems.DelayedSpawnSystem;

public class MapObjectBuilder {

    private final TiledMapReader mapReader;
    private final Engine engine;
    private final EntityFactory entityFactory;
    private final DelayedSpawnSystem delayedSpawnSystem;
    private Entity p;

    public MapObjectBuilder(DelayedSpawnSystem delayedSpawnSystem, TiledMapReader mapReader, Engine engine, EntityFactory entityFactory) {
        this.delayedSpawnSystem = delayedSpawnSystem;
        this.mapReader = mapReader;
        this.engine = engine;
        this.entityFactory = entityFactory;
    }

    public void loadObjects()
    {
        Array<TiledMapReader.MapTileObject> mapObjects = mapReader.getTileObjects("static_objects");
        for(TiledMapReader.MapTileObject m : mapObjects)
        {
            String type = m.getString("type", "");
            String identity = m.getString("identity","");

            switch (type)
            {
                case "static" -> engine.addEntity(entityFactory.createStaticObjects(m));
                case "goblin" -> engine.addEntity(entityFactory.createGoblin(m));
                case "bad_goblin" -> engine.addEntity(entityFactory.createBadGoblin(m,p));
                case "skeleton" -> engine.addEntity(entityFactory.createSkeleton(m,identity));
                default -> engine.addEntity(entityFactory.createStaticObjects(m));
            }
        }

        Array<TiledMapReader.MapTileObject> mapObjects2 = mapReader.getTileObjects("roofs");
        if(mapObjects2 != null)
        {
            for(TiledMapReader.MapTileObject m : mapObjects2)
            {
                String type = m.getString("type", "");
                Entity e = null;
                switch (type)
                {
                    case "static" -> e = entityFactory.createStaticObjects(m);
                }
                RoofComponent r = new RoofComponent();
                r.roof_id = m.getString("identity","");
                e.add(r);
                engine.addEntity(e);
            }
        }

        Array<TiledMapReader.MapShape> mapObjects3 = new Array<>();
        if(mapReader.hasLayer("roof_rec"))
        {
            mapObjects3 = mapReader.getShapes("roof_rec");
        }
        for(TiledMapReader.MapShape m : mapObjects3)
        {
            String target = m.getString("target_id", "");
            ImmutableArray<Entity> roofs = engine.getEntitiesFor(Family.all(RoofComponent.class).get());
            for(Entity roof : roofs)
            {
                RoofComponent r = roof.getComponent(RoofComponent.class);
                if(r.roof_id.equals(target))
                {
                    r.rectangles.add(new Rectangle(m.bounds));
                }
            }
        }

        Array<TiledMapReader.MapTileObject> mapObjects5 = mapReader.getTileObjects("static_but_characteristic_objects");
        for(TiledMapReader.MapTileObject m : mapObjects5)
        {
            String raw = m.getString("spawn_flags","");
            String nearId = m.getString("spawnNearDialog","");
            if(m.getString("type","").equals("collectable"))
            {
                if("".equals(raw))
                {
                    entityFactory.createCollectableObjects(m);
                }
                else
                {
                    String[] flags = raw.split("\\s*,\\s*");
                    float radius = m.getFloat("spawnRadius",0f);
                    delayedSpawnSystem.addPending(flags,
                        () -> entityFactory.createCollectableObjects(m),
                        nearId.isEmpty() ? null : nearId, radius);
                }
            }
        }
    }

    public void setP(Entity p) {
        this.p = p;
    }
}
