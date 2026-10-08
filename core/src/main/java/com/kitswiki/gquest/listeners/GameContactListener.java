package com.kitswiki.gquest.listeners;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;
import com.kitswiki.gquest.components.ContactComponent;

public class GameContactListener implements ContactListener {
    private final ComponentMapper<ContactComponent> c = ComponentMapper.getFor(ContactComponent.class);

    @Override
    public void beginContact(Contact contact) {
        handle(contact,true);
    }

    @Override
    public void endContact(Contact contact) {
        handle(contact, false);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private void handle(Contact contact, boolean begin) {
        Entity a = entityOf(contact.getFixtureA());
        Entity b = entityOf(contact.getFixtureB());
        if (a == null || b == null) return;

        update(a, b, begin);
        update(b, a, begin);
    }

    private void update(Entity self, Entity other, boolean begin)
    {
        ContactComponent c = this.c.get(self);
        if(c == null)
        {
            return;
        }

        if(begin)
        {
            c.touching.add(other);
        }
        else
        {
            c.touching.removeValue(other, true);
        }
    }

    private Entity entityOf(Fixture f) {
        Object data = f.getUserData();
        if (data instanceof Entity) return (Entity) data;

        data = f.getBody().getUserData();
        return data instanceof Entity ? (Entity) data : null;
    }

}
