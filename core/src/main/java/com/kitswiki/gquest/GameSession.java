package com.kitswiki.gquest;

import com.badlogic.gdx.assets.AssetManager;
import com.kitswiki.gquest.components.HealthComponent;
import com.kitswiki.gquest.components.InventoryComponent;
import com.kitswiki.gquest.dialog.DialogManager;
import com.kitswiki.gquest.dialog.GameState;
import com.kitswiki.gquest.dialog.ShopManager;

public class GameSession {

    private final Main main;
    private final AssetManager assetManager;
    private final GameState gameState;
    private final DialogManager dialogManager;
    private final ShopManager shopManager;
    private final InventoryComponent i;
    private final HealthComponent h;

    public GameSession(Main main, AssetManager assetManager, GameState gameState, DialogManager dialogManager,ShopManager shopManager, InventoryComponent i,
                       HealthComponent h) {
        this.main = main;
        this.assetManager = assetManager;
        this.gameState = gameState;
        this.dialogManager = dialogManager;
        this.shopManager = shopManager;
        this.i = i;
        this.h = h;
    }

    public HealthComponent getHealthC()
    {
        return h;
    }

    public InventoryComponent getInv() {
        return i;
    }

    public DialogManager getDialogManager() {
        return dialogManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public GameState getGameState() {
        return gameState;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public Main getMain() {
        return main;
    }
}
