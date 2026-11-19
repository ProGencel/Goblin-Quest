package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Rectangle;
import com.kitswiki.gquest.components.LightComponent;
import com.kitswiki.gquest.components.RoofComponent;

public class LightSystem extends IteratingSystem {

    private final ComponentMapper<LightComponent> l = ComponentMapper.getFor(LightComponent.class);
    private final ComponentMapper<RoofComponent> r = ComponentMapper.getFor(RoofComponent.class);

    public LightSystem() {
        super(Family.all(LightComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        LightComponent l = this.l.get(entity);
        if(l.targetRoof != null)
        {
            RoofComponent r = this.r.get(l.targetRoof);
            isUnderRoof(r,l);
        }
    }

    private void isUnderRoof(RoofComponent r, LightComponent l)
    {
        if(r.alpha < 0.5f)
        {
            l.pointLight.setActive(true);
            return;
        }
        l.pointLight.setActive(false);
    }
}
