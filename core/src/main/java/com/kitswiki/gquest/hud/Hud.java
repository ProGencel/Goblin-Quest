package com.kitswiki.gquest.hud;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kitswiki.gquest.hud.customActors.ClockLabel;
import com.kitswiki.gquest.hud.customActors.DialogBox;
import com.kitswiki.gquest.systems.DayNightSystem;

public class Hud {

    private final Stage stage;
    private final Stack stack;

    private final DialogBox dialogBox;
    private final ClockLabel clockLabel;

    public Hud(Stage stage, AssetManager assetManager, DayNightSystem dayNightSystem) {
        this.stage = stage;
        this.stack = new Stack();
        stack.setFillParent(true);

        Skin skin = assetManager.get("UI/dialog/skin/skin.json");
        this.dialogBox = new DialogBox(skin);
        this.clockLabel = new ClockLabel(skin,dayNightSystem);
    }

    public void setDialog()
    {

        Table dialogTable = new Table();
        dialogTable.bottom();
        dialogTable.add(dialogBox).growX().top().left().pad(10).height(100);

        Table clockTable = new Table();
        clockTable.top();
        clockTable.add(clockLabel).expandX().top().right().pad(10);

        stack.add(dialogTable);
        stack.add(clockTable);

        stage.addActor(stack);
    }

    public DialogBox getDialogBox() {
        return dialogBox;
    }
}
