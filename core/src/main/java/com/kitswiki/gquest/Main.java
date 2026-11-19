package com.kitswiki.gquest;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kitswiki.gquest.components.HealthComponent;
import com.kitswiki.gquest.components.InventoryComponent;
import com.kitswiki.gquest.datas.SavedData;
import com.kitswiki.gquest.enums.BACK_MUSIC;
import com.kitswiki.gquest.hud.DialogManager;
import com.kitswiki.gquest.hud.GameState;
import com.kitswiki.gquest.hud.ShopManager;
import com.kitswiki.gquest.managers.AudioManager;
import com.kitswiki.gquest.screens.GameScreen;
import com.kitswiki.gquest.screens.MainMenuScreen;
import com.progencel.kitswikiengine.crypto.DataManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    private final DataManager dataManager = new DataManager("Goblin Quest");
    private final InventoryComponent i = new InventoryComponent();

    private AssetManager assetManager;
    private DialogManager dialogManager;
    private ShopManager shopManager;
    private GameState gameState;

    @Override
    public void create() {

        this.assetManager = new AssetManager();
        this.dialogManager = new DialogManager(i);
        this.shopManager = new ShopManager();
        AudioManager audioManager = new AudioManager(assetManager);

        loadAssets(assetManager);
        this.gameState = new GameState();
        loadData();

        GameSession gameSession = new GameSession(this,assetManager,audioManager,gameState,dialogManager,shopManager,i, new HealthComponent());

//        setScreen(new GameScreen(gameSession,"TiledProject/maps/town.tmx",
//            "begin", false, BACK_MUSIC.TOWN));

        setScreen(new MainMenuScreen(gameSession,this));
    }

    private void loadData()
    {
        try {
            gameState.readFrom(dataManager.load(SavedData.class));
        }catch (Exception e)
        {
            Gdx.app.error("SAVE","Save cannot loaded");
        }
    }

    public void saveGame()
    {
        SavedData data = new SavedData();
        gameState.writeTo(data);
        dataManager.save(data);
    }

    private void loadAssets(AssetManager assetManager)
    {
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load("TiledProject/maps/town.tmx", TiledMap.class);
        assetManager.load("TiledProject/maps/cave.tmx", TiledMap.class);
        assetManager.load("atlas/cooked/gquest.atlas", TextureAtlas.class);
        assetManager.load("UI/dialog/skin/skin.json", Skin.class);
        assetManager.load("UI/ui/skin/skin.json", Skin.class);
        assetManager.load("audio/walk.wav", Sound.class);
        assetManager.load("audio/TownTheme.mp3", Music.class);
        assetManager.load("audio/cave.ogg", Music.class);
        assetManager.load("audio/dialog.wav", Sound.class);
        assetManager.finishLoading();
    }

    @Override
    public void setScreen(Screen screen) {
        Screen old = getScreen();
        super.setScreen(screen);
        if(old != null && old != screen)
        {
            old.dispose();
        }
    }

    @Override
    public void dispose() {
        Screen current = getScreen();
        //saveGame();           TODO : After build change this and on everywhere !!!!!
        super.dispose();
        if(current != null)
        {
            current.dispose();
            assetManager.dispose();
        }
    }

}
