package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.kitswiki.gquest.ai.EnemyState;
import com.kitswiki.gquest.components.*;
import com.kitswiki.gquest.dialog.GameState;
import com.kitswiki.gquest.enums.CharacterState;
import com.kitswiki.gquest.dialog.DialogBox;
import com.kitswiki.gquest.dialog.DialogManager;

public class DialogInteractSystem extends IteratingSystem {

    private final ComponentMapper<DialogComponent> d = ComponentMapper.getFor(DialogComponent.class);
    private final ComponentMapper<PlayerComponent> p = ComponentMapper.getFor(PlayerComponent.class);
    private final ComponentMapper<ContactComponent> c = ComponentMapper.getFor(ContactComponent.class);
    private final ComponentMapper<StateComponent> s = ComponentMapper.getFor(StateComponent.class);
    private final ComponentMapper<InteractComponent> i = ComponentMapper.getFor(InteractComponent.class);

    private final Entity player;
    private final DialogBox dialogBox;
    private final DialogManager dialogReader;
    private final GameState gameState;

    public DialogInteractSystem(Entity player, DialogBox dialogBox, DialogManager dialogReader, GameState gameState) {
        super(Family.all(TransformComponent.class, DialogComponent.class).get());
        this.player = player;
        this.dialogBox = dialogBox;
        this.dialogReader = dialogReader;
        this.gameState = gameState;
    }

    @Override
    public void update(float deltaTime) {
        PlayerComponent p = this.p.get(player);

        if(dialogBox.isOpen())
        {
            if(dialogBox.isChoosing() && p.menuMove != 0)
            {
                if(p.menuMove == 2)
                {
                    dialogBox.confirmChoice();
                    onDialogClose(p);
                }
                else {
                    dialogBox.moveSelection(p.menuMove);
                }

            }
            if(p.interact)
            {
                dialogBox.advance();
                if(!dialogBox.isOpen())
                {
                    onDialogClose(p);
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
            startDialog(d.get(entity).dialogId);
        }
    }

    private void startDialog(String dialogId)
    {
        DialogManager.DialogEntry e = dialogReader.pick(dialogId,gameState);
        dialogBox.start(e.lines,dialogReader.visibleChoices(e,gameState));
    }

    private void onDialogClose(PlayerComponent p)
    {
        p.interact = false;
        DialogManager.Choice c = dialogBox.consumePicked();
        if(c != null)
        {
            if(c.set != null)
            {
                for(String f : c.set)
                {
                    gameState.set(f);
                }
            }
            if(c.next != null)
            {
                startDialog(c.next);
                return;
            }
        }
        s.get(player).set(EnemyState.IDLE);
    }
}
