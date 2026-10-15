package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.kitswiki.gquest.components.BodyComponent;
import com.kitswiki.gquest.components.TransformComponent;

public class PhysicSystem extends EntitySystem implements EntityListener{

    private final ComponentMapper<BodyComponent> b = ComponentMapper.getFor(BodyComponent.class);

    private final World world;
    private float accumulator;

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(Family.all(BodyComponent.class).get(), this);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        engine.removeEntityListener(this);
    }

    public PhysicSystem(World world) {
        this.world = world;
    }

    @Override
    public void update(float deltaTime) {
        doPhysicsStep(deltaTime);
    }

    private void doPhysicsStep(float deltaTime) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= 1/60f) {
            world.step(1/60f,6,2);
            accumulator -= 1/60f;
        }
    }

    @Override
    public void entityAdded(Entity entity) {

    }

    @Override
    public void entityRemoved(Entity entity) {
        BodyComponent b = this.b.get(entity);
        if(b.body != null)
        {
            world.destroyBody(b.body);
            b.body = null;
        }
    }
}
