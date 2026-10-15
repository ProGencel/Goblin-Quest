package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.kitswiki.gquest.components.CollectableComponent;
import com.kitswiki.gquest.components.InventoryComponent;
import com.kitswiki.gquest.components.ContactComponent;
import com.kitswiki.gquest.components.PlayerComponent;
import com.kitswiki.gquest.dialog.GameState;

public class CollectableInteractSystem extends IteratingSystem {

    private final ComponentMapper<ContactComponent> con = ComponentMapper.getFor(ContactComponent.class);
    private final ComponentMapper<InventoryComponent> i = ComponentMapper.getFor(InventoryComponent.class);
    private final ComponentMapper<CollectableComponent> c = ComponentMapper.getFor(CollectableComponent.class);
    private final ComponentMapper<PlayerComponent> p = ComponentMapper.getFor(PlayerComponent.class);

    private final GameState gameState;

    public CollectableInteractSystem(GameState gameState) {
        super(Family.all(ContactComponent.class, InventoryComponent.class).get());
        this.gameState = gameState;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ContactComponent con  = this.con.get(entity);
        InventoryComponent i = this.i.get(entity);
        PlayerComponent p = this.p.get(entity);
        CollectableComponent c;

        for(Entity e : con.touching)
        {
            c = this.c.get(e);
            if(c == null || !p.interact)
            {
                continue;
            }
            i.inventory.add(c.id);
            getEngine().removeEntity(e);
            gameState.set("duckGob.duck_collected");
        }
    }
}
