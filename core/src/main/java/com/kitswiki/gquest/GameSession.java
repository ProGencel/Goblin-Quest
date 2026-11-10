package com.kitswiki.gquest;

import com.badlogic.gdx.assets.AssetManager;
import com.kitswiki.gquest.components.HealthComponent;
import com.kitswiki.gquest.components.InventoryComponent;
import com.kitswiki.gquest.hud.DialogManager;
import com.kitswiki.gquest.hud.GameState;
import com.kitswiki.gquest.hud.ShopManager;

public class GameSession {

    private final Main main;
    private final AssetManager assetManager;
    private final GameState gameState;
    private final DialogManager dialogManager;
    private final ShopManager shopManager;
    private final InventoryComponent i;
    private final HealthComponent h;

    private int day = 1;
    private float hour = 8f;

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

    public void setHealth(float health)
    {
        h.currentHealth = health;
    }

    public float getHour() {
        return hour;
    }

    public void setHour(float hour) {
        this.hour = hour;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
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
