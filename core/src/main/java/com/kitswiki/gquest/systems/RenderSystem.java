package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.kitswiki.gquest.components.TextureComponent;
import com.kitswiki.gquest.components.TransformComponent;
import com.kitswiki.gquest.utils.Constants;

public class RenderSystem extends IteratingSystem {

    private final ComponentMapper<TextureComponent> tc = ComponentMapper.getFor(TextureComponent.class);
    private final ComponentMapper<TransformComponent> tsc = ComponentMapper.getFor(TransformComponent.class);

    private final SpriteBatch batch;

    public RenderSystem(SpriteBatch batch) {
        super(Family.all(TransformComponent.class, TextureComponent.class).get());
        this.batch = batch;
    }

    @Override
    public void update(float deltaTime) {
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Vector2 pos = tsc.get(entity).position;

        for(TextureRegion region : tc.get(entity).regions)
        {
            float w = region.getRegionWidth() * Constants.UNIT_SCALE;
            float h = region.getRegionHeight() * Constants.UNIT_SCALE;
            float x = pos.x - w/2;
            float y = pos.y - h/2;

            batch.draw(region,x,y,w,h);
        }
    }
}
