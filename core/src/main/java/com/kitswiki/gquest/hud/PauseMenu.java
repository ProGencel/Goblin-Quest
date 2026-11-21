package com.kitswiki.gquest.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kitswiki.gquest.hud.mainMenu.SettingdDialog;

public class PauseMenu {

    private final Stage stage;
    private final Skin skin;
    private final Preferences prefs = SettingdDialog.prefs;

    private final Runnable onResume;
    private final Runnable onMainMenu;
    private Dialog dialog;

    private int volume;

    public PauseMenu(Skin skin, SpriteBatch batch, I18NBundle bundle,
                     Runnable onResume, Runnable onMainMenu) {
        this.stage = new Stage(new FitViewport(640, 360), batch);
        this.skin = skin;
        this.onResume = onResume;
        this.onMainMenu = onMainMenu;
        this.volume = prefs.getInteger("vol", 50);

        set(bundle);
    }

    private void set(I18NBundle bundle)
    {
        setDialog(bundle);
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(skin.newDrawable("white", 0f, 0f, 0f, 0.6f));

        Table panel = new Table();
        panel.setBackground(skin.getDrawable("uibrown"));
        panel.pad(20);

        Label title = new Label(bundle.get("pause"), skin);
        title.setAlignment(Align.center);

        Table content = new Table();
        content.pad(20);
        content.add(createSoundTable(bundle)).pad(15);

        TextButton resumeButton = new TextButton(bundle.get("continue"), skin);
        resumeButton.pad(7);
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                saveVolume();
                if (onResume != null)
                {
                    onResume.run();
                    setActive(false);
                }
            }
        });

        TextButton mainMenuButton = new TextButton(bundle.get("mainMenu"), skin);
        mainMenuButton.pad(7);
        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                saveVolume();
                dialog.show(stage);
            }
        });

        Table buttonTable = new Table();
        buttonTable.padBottom(15);
        buttonTable.add(resumeButton).padRight(10);
        buttonTable.add(mainMenuButton);

        panel.add(title).padTop(20).row();
        panel.add(content).row();
        panel.add(buttonTable);

        root.add(panel);
        stage.addActor(root);

        setActive(false);
    }

    private Table createSoundTable(I18NBundle bundle)
    {
        Slider slider = createSlider(skin);
        slider.setValue(volume);

        Label valueLabel = new Label(String.valueOf(volume), skin);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int value = (int) slider.getValue();
                valueLabel.setText(value);
                volume = value;
                prefs.putInteger("vol", volume);
            }
        });

        Table soundTable = new Table();
        soundTable.add(new Label(bundle.get("sound"), skin)).left().padRight(20);
        soundTable.add(slider).width(200);
        soundTable.add(valueLabel).width(50).padLeft(5);
        return soundTable;
    }

    private void saveVolume()
    {
        prefs.putInteger("vol", volume);
        prefs.flush();
    }

    private static Slider createSlider(Skin skin) {
        Slider.SliderStyle style = new Slider.SliderStyle(
            skin.get("default-horizontal", Slider.SliderStyle.class));

        if (style.background != null) {
            Drawable bg = skin.newDrawable(style.background);
            if (bg instanceof BaseDrawable) {
                ((BaseDrawable) bg).setLeftWidth(0);
                ((BaseDrawable) bg).setRightWidth(0);
            }
            style.background = bg;
        }
        return new Slider(0f, 100f, 1f, false, style);
    }

    private void setDialog(I18NBundle bundle)
    {
        String sure = bundle.get("sure");
        String yes = bundle.get("yes");
        String cancel = bundle.get("cancel");
        String confirm = bundle.get("confirm");

        dialog = new Dialog(confirm, skin) {
            @Override
            protected void result(Object object) {
                if(!(boolean) object)
                {
                    if (onMainMenu != null)
                    {
                        onMainMenu.run();
                    }
                }
            }
        };

        dialog.setMovable(false);
        dialog.setResizable(false);

        dialog.text(sure);
        dialog.getTitleLabel().setAlignment(Align.center);
        dialog.getTitleTable().padTop(25).padBottom(10);
        dialog.getContentTable().pad(20);

        dialog.pad(20);
        dialog.getButtonTable().defaults().pad(10);

        dialog.button(cancel, true);
        dialog.button(yes, false);
    }

    public void setActive(boolean active)
    {
        if (!active)
        {
            saveVolume();
        }
        stage.getRoot().setVisible(active);
        stage.getRoot().setTouchable(active ? Touchable.enabled : Touchable.disabled);
        if (active) {
            Gdx.input.setInputProcessor(stage);
        }
    }

    public void resize(int width, int height)
    {
        stage.getViewport().update(width, height, true);
    }

    public void render()
    {
        stage.act();
        stage.draw();
    }

    public void dispose()
    {
        stage.dispose();
    }
}
