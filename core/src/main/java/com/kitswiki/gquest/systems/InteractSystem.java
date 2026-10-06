package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.kitswiki.gquest.components.ContactComponent;
import com.kitswiki.gquest.components.InteractComponent;
import com.kitswiki.gquest.components.PlayerComponent;

public class InteractSystem extends IteratingSystem {

    private final ComponentMapper<PlayerComponent> p = ComponentMapper.getFor(PlayerComponent.class);
    private final ComponentMapper<ContactComponent> c = ComponentMapper.getFor(ContactComponent.class);
    private final ComponentMapper<InteractComponent> i = ComponentMapper.getFor(InteractComponent.class);

    public InteractSystem()
    {
        super(Family.all(PlayerComponent.class, ContactComponent.class, InteractComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if(!p.get(entity).interact)
        {
            return;
        }

        for(Entity other : c.get(entity).touching)
        {
            i.get(other).contact = true;
        }
    }
}
