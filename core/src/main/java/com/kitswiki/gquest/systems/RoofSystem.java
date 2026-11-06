package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.kitswiki.gquest.components.BodyComponent;
import com.kitswiki.gquest.components.PlayerComponent;
import com.kitswiki.gquest.components.RoofComponent;

public class RoofSystem extends IteratingSystem {

    private final float FADE_SPEED = 4f;

    private final ComponentMapper<RoofComponent> r = ComponentMapper.getFor(RoofComponent.class);

    private Entity player;
    private final Vector2 playerPos = new Vector2();

    public RoofSystem() {
        super(Family.all(RoofComponent.class).get());
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        player = getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        playerPos.set(player.getComponent(BodyComponent.class).body.getPosition());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        RoofComponent r = this.r.get(entity);
        boolean inside = false;
        for(Rectangle rec : r.rectangles)
        {
            if(rec.contains(playerPos))
            {
                inside = true;
                break;
            }
        }

        float dir = inside ? -1f : 1f;
        r.alpha = MathUtils.clamp(r.alpha + dir * FADE_SPEED * deltaTime,0f,1f);
    }
}
