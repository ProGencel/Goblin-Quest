package com.kitswiki.gquest.map;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
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

            switch (type)
            {
                case "static" -> engine.addEntity(entityFactory.createStaticObjects(m));
                case "goblin" -> engine.addEntity(entityFactory.createGoblin(m));
                case "bad_goblin" -> engine.addEntity(entityFactory.createBadGoblin(m,p));
            }
        }

        Array<TiledMapReader.MapTileObject> mapObjects2 = mapReader.getTileObjects("static_but_characteristic_objects");
        for(TiledMapReader.MapTileObject m : mapObjects2)
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
