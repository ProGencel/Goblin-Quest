package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.kitswiki.gquest.components.*;
import com.kitswiki.gquest.enums.CharacterState;
import com.kitswiki.gquest.ui.DialogBox;
import com.kitswiki.gquest.ui.DialogManager;

public class DialogInteractSystem extends IteratingSystem {

    private final ComponentMapper<DialogComponent> dialogM = ComponentMapper.getFor(DialogComponent.class);
    private final ComponentMapper<PlayerComponent> p = ComponentMapper.getFor(PlayerComponent.class);
    private final ComponentMapper<ContactComponent> c = ComponentMapper.getFor(ContactComponent.class);
    private final ComponentMapper<StateComponent> s = ComponentMapper.getFor(StateComponent.class);

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

        PlayerComponent p = this.p.get(player);
        ContactComponent c = this.c.get(player);
        if(!p.interact || c.touching.isEmpty())
        {
            return;
        }

        StateComponent s = this.s.get(player);

        if (dialogBox.isOpen()) {
            dialogBox.advance();
            if(!dialogBox.isOpen())
            {
                s.set(CharacterState.IDLE);
            }
            return;
        }

        s.set(CharacterState.STOP);

        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (dialogBox.isOpen())
        {
            return;
        }

        DialogComponent dialog = dialogM.get(entity);

        String[] lines = dialogReader.getLines(dialog.dialogId);
        dialogBox.start(lines);
    }
}
