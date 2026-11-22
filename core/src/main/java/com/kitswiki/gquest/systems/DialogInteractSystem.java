package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.kitswiki.gquest.ai.enemy.EnemyState;
import com.kitswiki.gquest.components.*;
import com.kitswiki.gquest.hud.customActors.DialogBox;
import com.kitswiki.gquest.hud.DialogManager;
import com.kitswiki.gquest.hud.GameState;
import com.kitswiki.gquest.hud.ShopManager;
import com.kitswiki.gquest.enums.CharacterState;
import com.kitswiki.gquest.managers.AudioManager;

public class DialogInteractSystem extends IteratingSystem {

    private final ComponentMapper<DialogComponent> d = ComponentMapper.getFor(DialogComponent.class);
    private final ComponentMapper<PlayerComponent> p = ComponentMapper.getFor(PlayerComponent.class);
    private final ComponentMapper<StateComponent> s = ComponentMapper.getFor(StateComponent.class);
    private final ComponentMapper<InventoryComponent> inv = ComponentMapper.getFor(InventoryComponent.class);
    private final ComponentMapper<SkeletonAIComponent> ske = ComponentMapper.getFor(SkeletonAIComponent.class);

    private final Entity player;
    private final DialogBox dialogBox;
    private final DialogManager dialogReader;
    private final GameState gameState;
    private final ShopManager shopManager;
    private final Runnable changeToFinal;

    private String currentDialogId;
    private DialogManager.DialogEntry currentEntry;

    private ShopManager.ShopItem[] shopItems;
    private Array<DialogManager.Choice> shopChoices;


    public DialogInteractSystem(Entity player, DialogBox dialogBox, DialogManager dialogReader, GameState gameState, ShopManager shopManager,Runnable runnable) {
        super(Family.all(TransformComponent.class, DialogComponent.class).get());
        this.player = player;
        this.dialogBox = dialogBox;
        this.dialogReader = dialogReader;
        this.gameState = gameState;
        this.shopManager = shopManager;
        this.changeToFinal = runnable;
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
        Entity pInteractTarget = p.get(player).interactTarget;

        if(entity != pInteractTarget)
        {
            return;
        }

        if(p.get(player).interact && d.has(pInteractTarget))
        {
            if (s.has(pInteractTarget) && s.get(pInteractTarget).charState.equals(CharacterState.RUN))
            {
                return;
            }
            startDialog(d.get(entity).dialogId);
            s.get(player).set(CharacterState.STOP);
        }
    }

    public void executeAction(String action)
    {
        switch (action) {
            case "ske_cave" -> sendCaveSkes();
            case "final" -> changeToFinal.run();
            default -> throw new IllegalArgumentException("Unknown dialog action: " + action);
        }
    }

    private void startDialog(String dialogId)
    {
        currentDialogId = dialogId;
        show(dialogReader.pick(dialogId, gameState));
    }

    private void goToEntry(String entryId)
    {
        show(dialogReader.goTo(currentDialogId, entryId));
    }

    private void show(DialogManager.DialogEntry e)
    {
        currentEntry = e;

        dialogBox.start(e.lines, dialogReader.visibleChoices(e, gameState));
    }

    private void enterShop(String shopId)
    {
        shopItems = shopManager.getItems(shopId);

        shopChoices = new Array<>();
        for(ShopManager.ShopItem item : shopItems)
        {
            DialogManager.Choice c = new DialogManager.Choice();
            c.text = item.name + " - " + item.price + "g";
            shopChoices.add(c);
        }

        DialogManager.Choice leave = new DialogManager.Choice();
        leave.text = "Leave";
        shopChoices.add(leave);

        dialogBox.showShop(shopChoices, "Gold: " + gold());
    }

    private void handleShopChoice(DialogManager.Choice c)
    {
        int i = shopChoices.indexOf(c, true);

        if(i == shopItems.length)
        {
            shopItems = null;
            shopChoices = null;
            dialogBox.close();
            s.get(player).set(EnemyState.IDLE);
            return;
        }

        boolean ok = shopManager.tryBuy(shopItems[i], inv.get(player));
        dialogBox.setHeader("Gold: " + gold() + (ok ? " - Thanks!" : " - Not enough gold."));
    }

    private int gold()
    {
        return inv.get(player).gold;
    }

    private void onDialogClose(PlayerComponent p)
    {
        p.interact = false;
        DialogManager.Choice c = dialogBox.consumePicked();
        if(c != null)
        {
            if(c.shop != null)
            {
                enterShop(c.shop);
                return;
            }
            if(shopItems != null)
            {
                handleShopChoice(c);
                return;
            }

            if(c.set != null)
            {
                for(String f : c.set)
                {
                    gameState.set(f);
                }
            }
            if(c.next != null)
            {
                goToEntry(c.next);
                return;
            }
        }

        if(dialogBox.isOpen())
        {
            return;
        }

        if (currentEntry != null && currentEntry.action != null) {
            executeAction(currentEntry.action);
        }

        currentEntry = null;
        s.get(player).set(EnemyState.IDLE);
    }

    private void sendCaveSkes()
    {
        for(Entity e : getEngine().getEntitiesFor(Family.all(SkeletonAIComponent.class).get()))
        {
            e.getComponent(SkeletonAIComponent.class).skeletonAI.moveToCave();
        }
    }
}
