package com.kitswiki.gquest.factories;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.utils.Array;
import com.kitswiki.gquest.components.LightComponent;
import com.kitswiki.gquest.components.PlayerComponent;
import com.kitswiki.gquest.components.RoofComponent;
import com.kitswiki.gquest.map.TiledMapReader;
import com.kitswiki.gquest.utils.Constants;

public class LightFactory {

    private final TiledMapReader mapReader;
    private final RayHandler rayHandler;
    private final Engine engine;
    private final ImmutableArray<Entity> roofs;

    public LightFactory(TiledMapReader mapReader, RayHandler rayHandler, Engine engine) {
        this.mapReader = mapReader;
        this.rayHandler = rayHandler;
        this.engine = engine;

        this.roofs = engine.getEntitiesFor(Family.all(RoofComponent.class).get());
    }

    public void createLights()
    {
        Array<TiledMapReader.MapPoint> lights = mapReader.getPoints("lights");
        Color streetLight = new Color(1.00f, 0.78f, 0.48f, 0.85f);

        for(TiledMapReader.MapPoint m : lights)
        {
            Entity e = new Entity();

            LightComponent l = new LightComponent();
            PointLight p = new PointLight(rayHandler,64);
            p.setPosition(m.getPosition());
            p.setDistance(2.5f);
            p.setColor(streetLight);
            p.setStaticLight(true);
            p.setXray(true);
            l.pointLight = p;
            String target_id = m.getString("target_id","");

            if(m.has("range"))
            {
                p.setDistance(m.getFloat("range",2.5f));
                p.setStaticLight(false);
                p.setXray(false);
                p.setContactFilter((short) 0xFFFF, (short) 0, (short) ~Constants.PLAYER_FIX_BIT);
            }

            if(!target_id.isEmpty())
            {
                for(Entity r : roofs)
                {
                    if(r.getComponent(RoofComponent.class).roof_id.equals(target_id))
                    {
                        l.targetRoof = r;
                    }
                }
            }

            e.add(l);
            engine.addEntity(e);
        }
    }
}
