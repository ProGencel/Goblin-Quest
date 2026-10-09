package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.kitswiki.gquest.components.AIComponent;

public class AISystem extends IteratingSystem {

    private final ComponentMapper<AIComponent> ai = ComponentMapper.getFor(AIComponent.class);

    public AISystem() {
        super(Family.all(AIComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ai.get(entity).enemyAI.update(deltaTime);
    }
}
