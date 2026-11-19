package com.kitswiki.gquest.hud.mainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kitswiki.gquest.GameSession;
import com.kitswiki.gquest.Main;
import com.kitswiki.gquest.enums.BACK_MUSIC;
import com.kitswiki.gquest.screens.GameScreen;

import java.util.function.Supplier;

public class MainMenuBuilder {

    private final int SIZE = 4;

    private final Stage stage;
    private final Skin skin;
    private final Supplier<I18NBundle> bundleSupplier;

    public MainMenuBuilder(GameSession gameSession, Main main) {
        this.skin = gameSession.getAssetManager().get("UI/ui/skin/skin.json",Skin.class);
        this.stage = new Stage(new ScreenViewport());
        this.bundleSupplier = new Supplier<I18NBundle>() {
            @Override
            public I18NBundle get() {
                return gameSession.getBundle();
            }
        };
        //stage.setDebugAll(true);
        setTables(main,gameSession);

        Gdx.input.setInputProcessor(stage);
    }

    private void setTables(Main main, GameSession gameSession)
    {
        Table root = new Table(skin);
        root.setFillParent(true);
        root.setBackground(skin.getDrawable("uibrown"));

        Table textTable = new Table(skin);

        TextButton textButton = new TextButton(bundleSupplier.get().get("play"),skin);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.setScreen(new GameScreen(gameSession,"TiledProject/maps/town.tmx","begin", false, BACK_MUSIC.TOWN));
            }
        });

        TextButton textButton1 = new TextButton(bundleSupplier.get().get("options"),skin);
        textButton1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameSession.setLanguage(GameSession.LANGS.AZ);
            }
        });

        TextButton textButton2 = new TextButton(bundleSupplier.get().get("exit"),skin);




        sizela(textTable.add(textButton));
        sizela(textTable.add(textButton1));
        sizela(textTable.add(textButton2));

        root.add(textTable);
        stage.addActor(root);
    }

    public void render()
    {
        stage.act();
        stage.draw();
    }

    public Stage getStage() {
        return stage;
    }

    private <T extends Actor> Cell<T> sizela(Cell<Actor> cell)
    {
        return (Cell<T>) cell.size(cell.getPrefWidth() * SIZE,
            cell.getPrefHeight() * SIZE);
    }
}
