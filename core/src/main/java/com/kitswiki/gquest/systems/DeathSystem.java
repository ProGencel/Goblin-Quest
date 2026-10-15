package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.kitswiki.gquest.components.DeadComponent;
import com.kitswiki.gquest.components.HealthComponent;
import com.kitswiki.gquest.components.PlayerComponent;
import com.kitswiki.gquest.components.StateComponent;
import com.kitswiki.gquest.enums.CharacterState;
import com.kitswiki.gquest.interfaces.ChangeMap;

public class DeathSystem extends IteratingSystem {

    private final ComponentMapper<HealthComponent> h = ComponentMapper.getFor(HealthComponent.class);
    private final ComponentMapper<StateComponent> s = ComponentMapper.getFor(StateComponent.class);
    private final ComponentMapper<DeadComponent> d = ComponentMapper.getFor(DeadComponent.class);
    private final ComponentMapper<PlayerComponent> p = ComponentMapper.getFor(PlayerComponent.class);

    private final float SPAWN_TIME = 1.5f;
    private String respawnMap;
    private String respawnSpawn;

    private final ChangeMap screen;

    public DeathSystem(ChangeMap screen, String respawnMap, String respawnSpawn) {
        super(Family.all(HealthComponent.class).get());

        this.screen = screen;
        this.respawnMap = respawnMap;
        this.respawnSpawn = respawnSpawn;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        HealthComponent h = this.h.get(entity);
        StateComponent s = this.s.get(entity);
        DeadComponent d = this.d.get(entity);

        if(!d.dead)
        {
            if(h.currentHealth <= 0f)
            {
                d.dead = true;
                s.set(CharacterState.DEATH);
            }
            return;
        }
        d.timeSinceDeath += deltaTime;
        if(p.has(entity) && d.timeSinceDeath >= SPAWN_TIME)
        {
            screen.changeMap(respawnMap,respawnSpawn);
        }
    }
}
