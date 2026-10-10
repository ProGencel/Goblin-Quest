package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.kitswiki.gquest.components.DeadComponent;
import com.kitswiki.gquest.components.HealtComponent;
import com.kitswiki.gquest.components.StateComponent;
import com.kitswiki.gquest.enums.CharacterState;

public class DeathSystem extends IteratingSystem {

    private final ComponentMapper<HealtComponent> h = ComponentMapper.getFor(HealtComponent.class);
    private final ComponentMapper<StateComponent> s = ComponentMapper.getFor(StateComponent.class);
    private final ComponentMapper<DeadComponent> d = ComponentMapper.getFor(DeadComponent.class);

    public DeathSystem() {
        super(Family.all(HealtComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        HealtComponent h = this.h.get(entity);
        StateComponent s = this.s.get(entity);
        DeadComponent d = this.d.get(entity);

        if(h.currentHealth <= 0f && !s.charState.equals(CharacterState.DEATH))
        {
            d.dead = true;
            s.set(CharacterState.DEATH);
        }
    }
}
