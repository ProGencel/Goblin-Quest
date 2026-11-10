package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.kitswiki.gquest.ai.EnemyState;
import com.kitswiki.gquest.components.*;
import com.kitswiki.gquest.hud.customActors.DialogBox;
import com.kitswiki.gquest.hud.DialogManager;
import com.kitswiki.gquest.hud.GameState;
import com.kitswiki.gquest.hud.ShopManager;
import com.kitswiki.gquest.enums.CharacterState;

public class DialogInteractSystem extends IteratingSystem {

    private final ComponentMapper<DialogComponent> d = ComponentMapper.getFor(DialogComponent.class);
    private final ComponentMapper<PlayerComponent> p = ComponentMapper.getFor(PlayerComponent.class);
    private final ComponentMapper<StateComponent> s = ComponentMapper.getFor(StateComponent.class);
    private final ComponentMapper<InventoryComponent> inv = ComponentMapper.getFor(InventoryComponent.class);

    private final Entity player;
    private final DialogBox dialogBox;
    private final DialogManager dialogReader;
    private final GameState gameState;
    private final ShopManager shopManager;
    private String currentDialogId;

    private ShopManager.ShopItem[] shopItems;
    private Array<DialogManager.Choice> shopChoices;

    public DialogInteractSystem(Entity player, DialogBox dialogBox, DialogManager dialogReader, GameState gameState, ShopManager shopManager) {
        super(Family.all(TransformComponent.class, DialogComponent.class).get());
        this.player = player;
        this.dialogBox = dialogBox;
        this.dialogReader = dialogReader;
        this.gameState = gameState;
        this.shopManager = shopManager;
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
            startDialog(d.get(entity).dialogId);
            s.get(player).set(CharacterState.STOP);
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
        s.get(player).set(EnemyState.IDLE);
    }
}
