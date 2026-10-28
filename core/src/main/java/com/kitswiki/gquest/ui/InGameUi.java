package com.kitswiki.gquest.ui;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kitswiki.gquest.components.InventoryComponent;

public class InGameUi {

    private final InventoryComponent i;

    private final Stage stage;
    private final Skin skin;
    private final TextureAtlas atlas;

    private Label label;

    public InGameUi(AssetManager assetManager, InventoryComponent i) {

        this.stage = new Stage(new FitViewport(800,450));
        this.skin = assetManager.get("UI/dialog/skin/skin.json");
        this.atlas = assetManager.get("atlas/cooked/gquest.atlas");
        this.i = i;
        setTables();
    }

    private void setTables()
    {
        Table root = new Table();
        root.setFillParent(true);

        Table leftTopTable = new Table();
        leftTopTable.pad(15);

        label = new Label("0",skin);
        Image image = new Image(atlas.findRegion("gold"));

        Table goldTable = new Table();
        goldTable.add(image).padRight(10).size(32,32);
        goldTable.add(label);

        leftTopTable.add(goldTable);

        root.add(leftTopTable).expand().top().left();
        stage.addActor(root);
    }

    public void render()
    {
        label.setText(i.gold);
        stage.act();
        stage.draw();
    }

}
