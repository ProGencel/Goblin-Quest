package com.kitswiki.gquest.listeners;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
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

    private void handle(Contact c, boolean begin)
    {
        Object a = c.getFixtureA().getBody().getUserData();
        Object b = c.getFixtureB().getBody().getUserData();
        if(!(a instanceof Entity) || !(b instanceof Entity))
        {
            return;
        }
        update((Entity) a, (Entity) b, begin);
        update((Entity) b, (Entity) a, begin);

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

}
