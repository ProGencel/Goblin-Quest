package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.kitswiki.gquest.components.BodyComponent;
import com.kitswiki.gquest.components.MovementComponent;

public class MovementSystem extends IteratingSystem {

    ComponentMapper<BodyComponent> b = ComponentMapper.getFor(BodyComponent.class);
    ComponentMapper<MovementComponent> m = ComponentMapper.getFor(MovementComponent.class);

    public MovementSystem() {
        super(Family.all(BodyComponent.class, MovementComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BodyComponent b = this.b.get(entity);
        MovementComponent m = this.m.get(entity);

        b.body.setLinearVelocity(m.dir.x * m.speed, m.dir.y * m.speed);
    }
}
