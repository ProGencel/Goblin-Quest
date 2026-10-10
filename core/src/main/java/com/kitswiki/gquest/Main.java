package com.kitswiki.gquest;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kitswiki.gquest.screens.GameScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    private Skin skin;

    @Override
    public void create() {

        AssetManager assetManager = new AssetManager();
        loadAssets(assetManager);
        this.skin = assetManager.get("UI/dialog/skin/skin.json");

        setScreen(new GameScreen(assetManager,this,"TiledProject/maps/town.tmx","begin", false));
    }

    private void loadAssets(AssetManager assetManager)
    {
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load("TiledProject/maps/town.tmx", TiledMap.class);
        assetManager.load("TiledProject/maps/cave.tmx", TiledMap.class);
        assetManager.load("atlas/cooked/gquest.atlas", TextureAtlas.class);
        assetManager.load("UI/dialog/skin/skin.json", Skin.class);
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
        super.dispose();
        if(current != null)
        {
            current.dispose();
            skin.dispose();
        }
    }

}
