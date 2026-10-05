package com.kitswiki.gquest.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kitswiki.gquest.factories.EntityFactory;
import com.kitswiki.gquest.map.StaticBodyBuilder;
import com.kitswiki.gquest.map.TiledMapReader;
import com.kitswiki.gquest.systems.*;
import com.kitswiki.gquest.ui.DialogUi;
import com.kitswiki.gquest.utils.Constants;

import static com.kitswiki.gquest.utils.Constants.UNIT_SCALE;

public class GameScreen implements Screen {

    private final AssetManager assetManager;
    private final TiledMap tiledMap;
    private final SpriteBatch batch;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final TiledMapReader mapReader;
    private final World world;

    private final Viewport dialogViewport;
    private final Stage dialogStage;
    private final DialogUi dialogUi;

    private final Engine engine;
    private final EntityFactory entityFactory;
    private final StaticBodyBuilder staticBodyBuilder;

    public GameScreen(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.tiledMap = assetManager.get("TiledProject/maps/town.tmx");
        this.batch = new SpriteBatch();
        this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, UNIT_SCALE,batch);
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(480f * UNIT_SCALE, 270f * UNIT_SCALE,camera);
        this.mapReader = new TiledMapReader(tiledMap, UNIT_SCALE);
        this.world = new World(new Vector2(0,0),true);

        this.dialogViewport = new FitViewport(480,270);
        this.dialogStage = new Stage(dialogViewport,batch);
        this.dialogUi = new DialogUi(dialogStage,assetManager.get("UI/dialog/skin/skin.json"));

        this.engine = new Engine();
        this.entityFactory = new EntityFactory(world,mapReader);
        this.staticBodyBuilder = new StaticBodyBuilder(world,mapReader);

        Vector2 spawn = mapReader.getPoint("objects","spawn").getPosition();
        camera.position.set(spawn,0);
    }

    @Override
    public void show() {
        engine.addSystem(new PhysicSystem(world));
        engine.addSystem(new PhysicSyncSystem());
        engine.addSystem(new CameraSystem(camera,mapReader,viewport));
        engine.addSystem(new AnimationSystem(assetManager.get("atlas/cooked/gquest.atlas")));
        engine.addSystem(new RenderSystem(batch,tiledMap,camera,mapRenderer,world,dialogStage));
        engine.addSystem(new MovementSystem(mapReader));
        engine.addSystem(new InputSystem());

        Array<TiledMapReader.MapTileObject> mapObjects = mapReader.getTileObjects("objects");

        for(TiledMapReader.MapTileObject m : mapObjects)
        {
            if(m.getString("type","").equals("static"))
            {
                float offsetY = m.getFloat("OFFSET_Y",0);
                int frameH = m.getInt("FRAME_H",48);
                engine.addEntity(entityFactory.createStaticObjects(m,offsetY,frameH));
            }
            else if(m.getString("type","").equals("goblin"))
            {
                float offsetY = m.getFloat("OFFSET_Y",0);
                int frameH = m.getInt("FRAME_H",0);
                engine.addEntity(entityFactory.createGoblin(m,offsetY,frameH));
            }
        }

        engine.addEntity(entityFactory.createPlayer(mapReader.getPoint("objects","spawn").getPosition()));

        staticBodyBuilder.createStaticBody();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0,0,0,1);
        engine.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        dialogViewport.update(width, height, true);
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
