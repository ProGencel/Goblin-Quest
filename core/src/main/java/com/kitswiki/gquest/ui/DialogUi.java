package com.kitswiki.gquest.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import static com.kitswiki.gquest.utils.Constants.UNIT_SCALE;

public class DialogUi {

    private final Stage stage;
    private final Skin skin;

    public DialogUi(Stage stage, Skin skin) {

        this.stage = stage;
        this.skin = skin;

        setTables();
    }

    private void setTables()
    {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.bottom();

        Table dialogTable = new Table();
        dialogTable.setBackground(skin.getDrawable("dialog"));

        Label label = new Label("Merhaba",skin);

        dialogTable.add(label).expand().top().left().pad(10);
        mainTable.add(dialogTable).growX().height(80).pad(18);

        stage.addActor(mainTable);
    }
}
