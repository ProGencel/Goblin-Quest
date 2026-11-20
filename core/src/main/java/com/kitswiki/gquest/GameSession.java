package com.kitswiki.gquest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import com.kitswiki.gquest.components.HealthComponent;
import com.kitswiki.gquest.components.InventoryComponent;
import com.kitswiki.gquest.hud.DialogManager;
import com.kitswiki.gquest.hud.GameState;
import com.kitswiki.gquest.hud.ShopManager;
import com.kitswiki.gquest.hud.mainMenu.SettingdDialog;
import com.kitswiki.gquest.managers.AudioManager;

import java.io.File;
import java.util.Locale;

public class GameSession {

    private final Main main;
    private final AssetManager assetManager;
    private final AudioManager audioManager;
    private final GameState gameState;
    private final DialogManager dialogManager;
    private final ShopManager shopManager;
    private final InventoryComponent i;
    private final HealthComponent h;

    private final ObjectMap<String, I18NBundle> bundles = new ObjectMap<>();
    private I18NBundle bundle;

    private int day = 1;
    private float hour = 8f;

    public GameSession(Main main, AssetManager assetManager, AudioManager audioManager, GameState gameState, DialogManager dialogManager,ShopManager shopManager, InventoryComponent i,
                       HealthComponent h) {
        this.main = main;
        this.assetManager = assetManager;
        this.audioManager = audioManager;
        this.gameState = gameState;
        this.dialogManager = dialogManager;
        this.shopManager = shopManager;
        this.i = i;
        this.h = h;

        this.setBundles();
        this.bundle = bundles.get(SettingdDialog.prefs.getString("lang"));

        dialogManager.readJson(bundle);
    }

    public void setLanguage(LANGS langs)
    {
        String lang;
        switch (langs)
        {
            case TR -> lang = "tr";
            case AZ -> lang = "az";
            default -> lang = "en";
        }

        bundle = bundles.get(lang);
        dialogManager.readJson(bundle);
    }

    private void setBundles()
    {
        String[] langs = {"en","tr","az"};
        FileHandle baseFile = Gdx.files.internal("data/localization/loc");

        for(String s : langs)
        {
            bundles.put(s,I18NBundle.createBundle(baseFile,new Locale(s)));
        }
    }

    public I18NBundle getBundle() {
        return bundle;
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

    public AudioManager getAudioManager() {
        return audioManager;
    }



    public enum LANGS
    {
        TR,
        AZ,
        EN
    }
}
