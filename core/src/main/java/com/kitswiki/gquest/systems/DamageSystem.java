package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.kitswiki.gquest.components.CanDamageComponent;
import com.kitswiki.gquest.components.HealtComponent;
import com.kitswiki.gquest.components.StateComponent;
import com.kitswiki.gquest.enums.CharacterState;

public class DamageSystem extends IteratingSystem {

    private ComponentMapper<HealtComponent> h = ComponentMapper.getFor(HealtComponent.class);
    private final ComponentMapper<CanDamageComponent> c = ComponentMapper.getFor(CanDamageComponent.class);
    private final ComponentMapper<StateComponent> s = ComponentMapper.getFor(StateComponent.class);

    private final Entity player;
    private final HealtComponent ph;

    private float elapsedTime = 0f;

    public DamageSystem(Entity player) {
        super(Family.all(CanDamageComponent.class).get());

        this.player = player;
        ph = this.h.get(player);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        elapsedTime += deltaTime;
        if(elapsedTime > ph.invincibleTime)
        {
            ph.isInvincible = false;
            elapsedTime = 0f;
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        StateComponent s = this.s.get(entity);
        CanDamageComponent c = this.c.get(entity);

        if(!ph.isInvincible && s.charState.equals(CharacterState.ATTACK))
        {
            ph.currentHealth -= c.damagePower;
            ph.isInvincible = true;
            System.out.println(ph.currentHealth);
        }
    }
}
