package com.kitswiki.gquest.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kitswiki.gquest.GameSession;
import com.kitswiki.gquest.Main;
import com.kitswiki.gquest.hud.mainMenu.MainMenuBuilder;
import com.kitswiki.gquest.managers.AudioManager;
import com.kitswiki.gquest.utils.Constants;

import static com.kitswiki.gquest.utils.Constants.UNIT_SCALE;

public class MainMenuScreen implements Screen {

    private final GameSession gameSession;
    private final MainMenuBuilder mainMenuBuilder;
    private final OrthographicCamera camera;
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Viewport viewport;

    public MainMenuScreen(GameSession gameSession, Main main) {
        this.gameSession = gameSession;
        this.mainMenuBuilder = new MainMenuBuilder(gameSession,main);
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(480f * UNIT_SCALE, 270f * UNIT_SCALE,camera);
        this.map = gameSession.getAssetManager().get("TiledProject/maps/menu.tmx");
        this.mapRenderer = new OrthogonalTiledMapRenderer(map, UNIT_SCALE);
    }

    @Override
    public void show() {
        gameSession.getAudioManager().playMusic(AudioManager.TYPE.MENU);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        viewport.apply();
        mapRenderer.setView(camera);
        mapRenderer.render();
        mainMenuBuilder.render();
        gameSession.getAudioManager().checkVolume();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height,true);
        mainMenuBuilder.getStage().getViewport().update(width, height,true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        //gameSession.getAudioManager().stopMusic();
    }

    @Override
    public void dispose() {
        mainMenuBuilder.dispose();
    }
}
