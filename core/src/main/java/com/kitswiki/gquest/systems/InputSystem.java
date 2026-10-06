package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.kitswiki.gquest.components.MovementComponent;
import com.kitswiki.gquest.components.PlayerComponent;

public class InputSystem extends IteratingSystem {

    private final ComponentMapper<MovementComponent> m = ComponentMapper.getFor(MovementComponent.class);
    private final ComponentMapper<PlayerComponent> p = ComponentMapper.getFor(PlayerComponent.class);

    public InputSystem() {
        super(Family.all(PlayerComponent.class, MovementComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        MovementComponent m = this.m.get(entity);
        PlayerComponent p = this.p.get(entity);

        p.interact = Gdx.input.isKeyJustPressed(Input.Keys.E);

        m.dir.set(0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) m.dir.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) m.dir.y -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) m.dir.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) m.dir.x += 1;
        m.dir.nor();
    }
}
