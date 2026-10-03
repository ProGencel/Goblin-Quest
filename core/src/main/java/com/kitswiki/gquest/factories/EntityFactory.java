package com.kitswiki.gquest.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.kitswiki.gquest.components.AnimationComponent;
import com.kitswiki.gquest.components.StateComponent;
import com.kitswiki.gquest.components.TextureComponent;
import com.kitswiki.gquest.components.TransformComponent;

public class EntityFactory {

    public Entity createPlayer(Vector2 pos)
    {
        Entity e = new Entity();

        TransformComponent t = new TransformComponent();
        t.position.set(pos);

        AnimationComponent a = new AnimationComponent();
        a.layers.add("base");

        e.add(t);
        e.add(new StateComponent());
        e.add(a);
        e.add(new TextureComponent());
        return e;
    }

}
