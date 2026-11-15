package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.kitswiki.gquest.ai.skeleton.SkeletonAI;
import com.kitswiki.gquest.components.HiddenComponent;
import com.kitswiki.gquest.components.SkeletonAIComponent;
import com.kitswiki.gquest.components.StateComponent;
import com.kitswiki.gquest.components.TextureComponent;

public class SkeletonAISystem extends IteratingSystem {

    private final ComponentMapper<SkeletonAIComponent> ai = ComponentMapper.getFor(SkeletonAIComponent.class);
    private final ComponentMapper<StateComponent> s = ComponentMapper.getFor(StateComponent.class);
    private final ComponentMapper<HiddenComponent> h = ComponentMapper.getFor(HiddenComponent.class);

    public SkeletonAISystem() {
        super(Family.all(SkeletonAIComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SkeletonAI brain = ai.get(entity).skeletonAI;

        brain.update(deltaTime);
        s.get(entity).set(brain.getStateMachine().getCurrentState());

        TextureComponent texture = entity.getComponent(TextureComponent.class);
        Vector2 velocity = brain.getSteerableBody().getLinearVelocity();

        if (velocity.x > 0.1f) {
            texture.flipped = false;
        } else if (velocity.x < -0.1f) {
            texture.flipped = true;
        }

        boolean hidden = brain.isHidden();
        if(hidden != h.has(entity))
        {
            if(hidden)
            {
                entity.add(new HiddenComponent());
            }
            else
            {
                entity.remove(HiddenComponent.class);
            }
        }
    }
}
