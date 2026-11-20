package com.kitswiki.gquest.hud.mainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.kitswiki.gquest.GameSession;

public class SettingdDialog extends Dialog {

    public static Preferences prefs = Gdx.app.getPreferences("KitsWiki Settings-GQuest");
    private int volume = 50;

    public SettingdDialog(String title, Skin skin, I18NBundle bundle, MainMenuBuilder builder) {
        super(title,createStyle(skin));

        setModal(true);
        setMovable(false);
        setResizable(false);
        getTitleLabel().setAlignment(Align.center);
        getTitleTable().padTop(20);
        getContentTable().pad(20);
        getButtonTable().padBottom(15);
        TextButton closeButton = new TextButton(bundle.get("close"),skin);
        closeButton.pad(7);

        TextButton applyButton = new TextButton(bundle.get("apply"),skin);
        applyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                prefs.putInteger("vol",volume);
                prefs.flush();
            }
        });
        applyButton.pad(7);
        button(closeButton);
        button(applyButton);
        key(Input.Keys.ESCAPE, null);
        setDialog(skin,builder);
    }

    private void setDialog(Skin skin,MainMenuBuilder builder)
    {
        SelectBox<String> selectBox = createSelectBox(skin, builder);

        Slider slider = createSlider(skin);

        Table content = getContentTable();

        Table lanTable = new Table();
        lanTable.add(new Label(builder.getGameSession().getBundle().get("lang"), skin)).left().padRight(20);
        lanTable.add(selectBox).row();

        Table soundTable = new Table();
        Label valueLabel = new Label(String.valueOf(returnLastVol()),skin);
        slider.setValue(returnLastVol());
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int value = (int)slider.getValue();
                valueLabel.setText(value);
                volume = value;
            }
        });
        soundTable.add(new Label(builder.getGameSession().getBundle().get("sound"),skin)).left().padRight(20);
        soundTable.add(slider).width(200);
        soundTable.add(valueLabel).width(50).padLeft(5);

        content.add(lanTable).pad(15).row();
        content.add(soundTable).pad(15);
    }

    private static SelectBox<String> createSelectBox(Skin skin, MainMenuBuilder builder) {
        SelectBox<String> selectBox = new SelectBox<>(skin);
        selectBox.setItems("Türkçe","English","Azərbaycanca");
        selectBox.setSelected(returnLastLang());
        selectBox.getStyle().background.setLeftWidth(10);


        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selected = selectBox.getSelected();
                switch (selected)
                {
                    case "Türkçe" -> {
                        builder.getGameSession().setLanguage(GameSession.LANGS.TR);
                        prefs.putString("lang","tr");
                        prefs.flush();
                        builder.buildUI();
                    }
                    case "Azərbaycanca" -> {
                        builder.getGameSession().setLanguage(GameSession.LANGS.AZ);
                        prefs.putString("lang","az");
                        prefs.flush();
                        builder.buildUI();
                    }
                    default-> {
                        builder.getGameSession().setLanguage(GameSession.LANGS.EN);
                        prefs.putString("lang","en");
                        prefs.flush();
                        builder.buildUI();
                    }
                }
            }
        });
        return selectBox;
    }

    private int returnLastVol()
    {
        return prefs.getInteger("vol");
    }

    private static String returnLastLang()
    {
        switch (prefs.getString("lang"))
        {
            case "tr" -> {
                return "Türkçe";
            }
            case "az" -> {
                return "Azərbaycanca";
            }
            default -> {
                return "English";
            }
        }
    }

    private static Window.WindowStyle createStyle(Skin skin) {
        Window.WindowStyle style = new Window.WindowStyle(skin.get(Window.WindowStyle.class));
        style.stageBackground = skin.newDrawable("white", 0f, 0f, 0f, 0.6f);
        return style;
    }

    private static Slider createSlider(Skin skin) {
        Slider.SliderStyle style = new Slider.SliderStyle(
            skin.get("default-horizontal", Slider.SliderStyle.class));

        if (style.background != null) {
            // skin'deki ortak drawable'ı bozmamak için kopyasını alıyoruz
            Drawable bg = skin.newDrawable(style.background);
            if (bg instanceof BaseDrawable) {
                ((BaseDrawable) bg).setLeftWidth(0);
                ((BaseDrawable) bg).setRightWidth(0);
            }
            style.background = bg;
        }
        return new Slider(0f, 100f, 1f, false, style);
    }
}
