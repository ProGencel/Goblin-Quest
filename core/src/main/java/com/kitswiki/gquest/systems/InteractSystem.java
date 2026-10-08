package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.kitswiki.gquest.components.ContactComponent;
import com.kitswiki.gquest.components.InteractComponent;
import com.kitswiki.gquest.components.PlayerComponent;
import com.kitswiki.gquest.components.StateComponent;
import com.kitswiki.gquest.enums.CharacterState;

public class InteractSystem extends EntitySystem {

    private final ComponentMapper<PlayerComponent> p = ComponentMapper.getFor(PlayerComponent.class);
    private final ComponentMapper<StateComponent> s = ComponentMapper.getFor(StateComponent.class);
    private final ComponentMapper<ContactComponent> c = ComponentMapper.getFor(ContactComponent.class);
    private final ComponentMapper<InteractComponent> i = ComponentMapper.getFor(InteractComponent.class);

    private final Entity player;

    public InteractSystem(Entity player) {
        this.player = player;
    }

    @Override
    public void update(float deltaTime) {
        PlayerComponent p = this.p.get(player);
        p.interactTarget = null;

        if(s.get(player).charState == CharacterState.STOP)
        {
            return;
        }

        for(Entity e : c.get(player).touching)
        {
            if(i.has(e))
            {
                p.interactTarget = e;
                return;
            }
        }
    }
}
