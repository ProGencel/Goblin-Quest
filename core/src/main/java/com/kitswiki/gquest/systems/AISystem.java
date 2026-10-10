package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.kitswiki.gquest.ai.EnemyAI;
import com.kitswiki.gquest.components.AIComponent;
import com.kitswiki.gquest.components.StateComponent;
import com.kitswiki.gquest.components.TextureComponent;

public class AISystem extends IteratingSystem {

    private final ComponentMapper<AIComponent> ai = ComponentMapper.getFor(AIComponent.class);
    private final ComponentMapper<StateComponent> s = ComponentMapper.getFor(StateComponent.class);
    private final ComponentMapper<TextureComponent> tex = ComponentMapper.getFor(TextureComponent.class);

    public AISystem() {
        super(Family.all(AIComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        EnemyAI enemyAI = ai.get(entity).enemyAI;
        enemyAI.update(deltaTime);

        s.get(entity).set(enemyAI.stateMachine.getCurrentState());
        TextureComponent tex = this.tex.get(entity);

        Vector2 facing = enemyAI.facing;

        if(facing.x > 0.7071f)
        {
            tex.flipped = false;
        }
        if(facing.x < -0.7071f)
        {
            tex.flipped = true;
        }
    }
}
