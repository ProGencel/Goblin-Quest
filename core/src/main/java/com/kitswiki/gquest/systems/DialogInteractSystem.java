package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.kitswiki.gquest.components.DialogComponent;
import com.kitswiki.gquest.components.TransformComponent;
import com.kitswiki.gquest.ui.DialogBox;
import com.kitswiki.gquest.ui.DialogManager;

public class DialogInteractSystem extends IteratingSystem {

    private static final float INTERACT_RANGE = 1.5f;

    private final ComponentMapper<TransformComponent> transformM =
        ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<DialogComponent> dialogM =
        ComponentMapper.getFor(DialogComponent.class);

    private final Entity player;
    private final DialogBox dialogBox;
    private final DialogManager dialogReader;

    public DialogInteractSystem(Entity player, DialogBox dialogBox, DialogManager dialogReader) {
        super(Family.all(TransformComponent.class, DialogComponent.class).get());
        this.player = player;
        this.dialogBox = dialogBox;
        this.dialogReader = dialogReader;
    }

    @Override
    public void update(float deltaTime) {
        if (!Gdx.input.isKeyJustPressed(Input.Keys.E)) return;

        if (dialogBox.isOpen()) {
            dialogBox.advance();
            return;
        }
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (dialogBox.isOpen()) return;

        TransformComponent npcT = transformM.get(entity);
        TransformComponent playerT = transformM.get(player);

        if (npcT.position.dst(playerT.position) > INTERACT_RANGE) return;

        DialogComponent dialog = dialogM.get(entity);
        String[] lines = dialogReader.getLines(dialog.dialogId);
        dialogBox.start(lines);
    }
}
