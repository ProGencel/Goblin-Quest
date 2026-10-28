package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.assets.AssetManager;
import com.kitswiki.gquest.components.InventoryComponent;
import com.kitswiki.gquest.ui.InGameUi;

public class InGameUiSystem extends EntitySystem {

    private final ComponentMapper<InventoryComponent> i = ComponentMapper.getFor(InventoryComponent.class);

    private final InGameUi inGameUi;

    public InGameUiSystem(AssetManager assetManager, Entity player) {
        inGameUi = new InGameUi(assetManager,i.get(player));
    }

    @Override
    public void update(float deltaTime) {
        inGameUi.render();
    }
}
