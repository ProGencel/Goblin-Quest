package com.kitswiki.gquest.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kitswiki.gquest.factories.EntityFactory;
import com.kitswiki.gquest.map.TiledMapReader;
import com.kitswiki.gquest.systems.AnimationSystem;
import com.kitswiki.gquest.systems.RenderSystem;
import com.kitswiki.gquest.utils.Constants;

public class GameScreen implements Screen {

    private final AssetManager assetManager;
    private final TiledMap tiledMap;
    private final SpriteBatch batch;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final TiledMapReader mapReader;

    private final Engine engine;
    private final EntityFactory entityFactory;

    public GameScreen(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.tiledMap = assetManager.get("TiledProject/maps/town.tmx");
        this.batch = new SpriteBatch();
        this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap,Constants.UNIT_SCALE,batch);
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(480f * Constants.UNIT_SCALE, 270f * Constants.UNIT_SCALE,camera);
        this.mapReader = new TiledMapReader(tiledMap,Constants.UNIT_SCALE);

        this.engine = new Engine();
        this.entityFactory = new EntityFactory();

        Vector2 spawn = mapReader.getPoint("objects","spawn");
        camera.position.set(spawn,0);
    }

    @Override
    public void show() {
        engine.addSystem(new AnimationSystem(assetManager.get("atlas/cooked/gquest.atlas")));
        engine.addSystem(new RenderSystem(batch));

        engine.addEntity(entityFactory.createPlayer(mapReader.getPoint("objects","spawn")));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0,0,0,1);
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
        engine.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
        mapRenderer.dispose();
    }
}
