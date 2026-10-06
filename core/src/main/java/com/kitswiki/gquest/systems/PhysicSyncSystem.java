package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.kitswiki.gquest.components.BodyComponent;
import com.kitswiki.gquest.components.PlayerComponent;
import com.kitswiki.gquest.components.StateComponent;
import com.kitswiki.gquest.components.TransformComponent;
import com.kitswiki.gquest.enums.CharacterState;

public class PhysicSyncSystem extends IteratingSystem {

    private final ComponentMapper<BodyComponent> b = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<TransformComponent> t = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<StateComponent> s = ComponentMapper.getFor(StateComponent.class);
    private final ComponentMapper<PlayerComponent> p = ComponentMapper.getFor(PlayerComponent.class);

    public PhysicSyncSystem() {
        super(Family.all(BodyComponent.class, TransformComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BodyComponent b = this.b.get(entity);
        TransformComponent t = this.t.get(entity);

        Object o = b.body.getUserData();
        if(o instanceof Entity && p.has(entity))
        {
            StateComponent s = this.s.get(entity);
            if(!s.charState.equals(CharacterState.STOP))
            {
                t.position.set(b.body.getPosition());
                return;
            }
        }

        t.position.set(b.body.getPosition());
    }
}
