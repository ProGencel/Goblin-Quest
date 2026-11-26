package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.kitswiki.gquest.components.CloudComponent;
import com.kitswiki.gquest.components.TransformComponent;

public class CloudSystem extends IteratingSystem {

    private final ComponentMapper<CloudComponent> c = ComponentMapper.getFor(CloudComponent.class);
    private final ComponentMapper<TransformComponent> t = ComponentMapper.getFor(TransformComponent.class);


    public CloudSystem() {
        super(Family.all(CloudComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CloudComponent cloud = c.get(entity);
        TransformComponent transform = t.get(entity);

        transform.position.x += deltaTime * cloud.speed;
    }
}
