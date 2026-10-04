package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.kitswiki.gquest.components.BodyComponent;
import com.kitswiki.gquest.components.MovementComponent;
import com.kitswiki.gquest.components.StateComponent;
import com.kitswiki.gquest.components.TextureComponent;
import com.kitswiki.gquest.enums.CharacterState;

public class MovementSystem extends IteratingSystem {

    ComponentMapper<BodyComponent> b = ComponentMapper.getFor(BodyComponent.class);
    ComponentMapper<MovementComponent> m = ComponentMapper.getFor(MovementComponent.class);
    ComponentMapper<TextureComponent> t = ComponentMapper.getFor(TextureComponent.class);
    ComponentMapper<StateComponent> s = ComponentMapper.getFor(StateComponent.class);

    public MovementSystem() {
        super(Family.all(BodyComponent.class, MovementComponent.class, TextureComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BodyComponent b = this.b.get(entity);
        MovementComponent m = this.m.get(entity);
        TextureComponent t = this.t.get(entity);
        StateComponent s = this.s.get(entity);

        if(!m.dir.equals(new Vector2(0,0)))
        {
            s.charState = CharacterState.RUN;
        }
        else
        {
            s.charState = CharacterState.IDLE;
        }
        if(m.dir.x < 0)
        {
            t.flipped = true;
        }
        else if(m.dir.x > 0)
        {
            t.flipped = false;
        }

        b.body.setLinearVelocity(m.dir.x * m.speed, m.dir.y * m.speed);
    }
}
