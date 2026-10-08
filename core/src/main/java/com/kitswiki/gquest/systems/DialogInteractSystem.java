package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.kitswiki.gquest.components.*;
import com.kitswiki.gquest.enums.CharacterState;
import com.kitswiki.gquest.ui.DialogBox;
import com.kitswiki.gquest.ui.DialogManager;

public class DialogInteractSystem extends IteratingSystem {

    private final ComponentMapper<DialogComponent> d = ComponentMapper.getFor(DialogComponent.class);
    private final ComponentMapper<PlayerComponent> p = ComponentMapper.getFor(PlayerComponent.class);
    private final ComponentMapper<ContactComponent> c = ComponentMapper.getFor(ContactComponent.class);
    private final ComponentMapper<StateComponent> s = ComponentMapper.getFor(StateComponent.class);
    private final ComponentMapper<InteractComponent> i = ComponentMapper.getFor(InteractComponent.class);

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

        if(dialogBox.isOpen())
        {
            if(p.interact)
            {
                dialogBox.advance();
                if(!dialogBox.isOpen())
                {
                    s.get(player).set(CharacterState.IDLE);
                    p.interact = false;
                }
            }
            return;
        }

        if(p.interactTarget != null)
        {
            super.update(deltaTime);
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if(entity != p.get(player).interactTarget)
        {
            return;
        }

        if(p.get(player).interact)
        {
            s.get(player).set(CharacterState.STOP);
            dialogBox.start(dialogReader.getLines(d.get(entity).dialogId));
        }
    }
}
