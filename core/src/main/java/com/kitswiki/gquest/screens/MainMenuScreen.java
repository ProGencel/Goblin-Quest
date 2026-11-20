package com.kitswiki.gquest.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kitswiki.gquest.GameSession;
import com.kitswiki.gquest.Main;
import com.kitswiki.gquest.hud.mainMenu.MainMenuBuilder;

public class MainMenuScreen implements Screen {

    private final GameSession gameSession;
    private final MainMenuBuilder mainMenuBuilder;

    public MainMenuScreen(GameSession gameSession, Main main) {
        this.gameSession = gameSession;
        this.mainMenuBuilder = new MainMenuBuilder(gameSession,main);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        mainMenuBuilder.render();
    }

    @Override
    public void resize(int width, int height) {
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

    }

    @Override
    public void dispose() {
        mainMenuBuilder.dispose();
    }
}
