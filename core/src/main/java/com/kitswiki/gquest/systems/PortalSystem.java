package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.kitswiki.gquest.components.PlayerComponent;
import com.kitswiki.gquest.components.PortalComponent;
import com.kitswiki.gquest.interfaces.ChangeMap;

public class PortalSystem extends IteratingSystem {

    private final ComponentMapper<PlayerComponent> p = ComponentMapper.getFor(PlayerComponent.class);
    private final ComponentMapper<PortalComponent> po = ComponentMapper.getFor(PortalComponent.class);

    private final ChangeMap screen;
    private final Entity player;

    public PortalSystem(ChangeMap screen, Entity player) {
        super(Family.all(PortalComponent.class).get());

        this.screen = screen;
        this.player = player;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent p = this.p.get(player);
        PortalComponent po = this.po.get(entity);

        if(p.interactTarget != entity)
        {
            return;
        }
        screen.changeMap(po.targetMap,po.targetSpawn);
    }
}
