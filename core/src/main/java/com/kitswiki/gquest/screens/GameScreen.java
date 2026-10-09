package com.kitswiki.gquest.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kitswiki.gquest.Main;
import com.kitswiki.gquest.ai.EnemyAI;
import com.kitswiki.gquest.ai.SteerableBody;
import com.kitswiki.gquest.components.BodyComponent;
import com.kitswiki.gquest.components.PlayerComponent;
import com.kitswiki.gquest.factories.EntityFactory;
import com.kitswiki.gquest.factories.PortalFactory;
import com.kitswiki.gquest.interfaces.ChangeMap;
import com.kitswiki.gquest.listeners.GameContactListener;
import com.kitswiki.gquest.map.StaticBodyBuilder;
import com.kitswiki.gquest.map.TiledMapReader;
import com.kitswiki.gquest.systems.*;
import com.kitswiki.gquest.ui.DialogBox;
import com.kitswiki.gquest.ui.DialogManager;

import static com.kitswiki.gquest.utils.Constants.UNIT_SCALE;

public class GameScreen implements Screen, ChangeMap {

    private final Main main;

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
    private final DialogManager dialogManager;
    private final DialogBox dialogBox;

    private final Engine engine;
    private final EntityFactory entityFactory;
    private final PortalFactory portalFactory;
    private final StaticBodyBuilder staticBodyBuilder;

    private final String mapPath;
    private final Vector2 spawnPos;

    private String pendingMap;
    private String pendingSpawn;

    private SteerableBody playerSteerable;
    private final Array<EnemyAI> enemies = new Array<>();

    public GameScreen(AssetManager assetManager, Main main, String mapPath, String spawnId) {
        this.main = main;
        this.assetManager = assetManager;
        this.mapPath = mapPath;
        this.tiledMap = assetManager.get(mapPath);
        this.batch = new SpriteBatch();
        this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, UNIT_SCALE,batch);
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(480f * UNIT_SCALE, 270f * UNIT_SCALE,camera);
        this.mapReader = new TiledMapReader(tiledMap, UNIT_SCALE);
        this.world = new World(new Vector2(0,0),true);
        this.world.setContactListener(new GameContactListener());

        this.dialogViewport = new FitViewport(480,270);
        this.dialogStage = new Stage(dialogViewport,batch);
        this.dialogManager = new DialogManager();
        this.dialogBox = new DialogBox(assetManager.get("UI/dialog/skin/skin.json"));

        setDialog();

        this.engine = new Engine();
        this.entityFactory = new EntityFactory(world,mapReader,engine);
        this.portalFactory = new PortalFactory(mapReader,engine,world);
        this.staticBodyBuilder = new StaticBodyBuilder(world,mapReader,engine);

        dialogManager.readJson();
        spawnPos = mapReader.getPointByProperty("spawns","spawnId",spawnId).getPosition();
        camera.position.set(spawnPos,0);
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

        Entity p = entityFactory.createPlayer(spawnPos);
        engine.addEntity(p);
        playerSteerable = new SteerableBody(p.getComponent(BodyComponent.class).body,0.5f);

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
            else if(m.getString("type","").equals("bad_goblin"))
            {
                float offsetY = m.getFloat("OFFSET_Y",0);
                int frameH = m.getInt("FRAME_H",0);
                Entity badGoblin = entityFactory.createBadGoblin(m,offsetY,frameH);
                engine.addEntity(badGoblin);
                SteerableBody steerable = new SteerableBody(badGoblin.getComponent(BodyComponent.class).body, 0.5f);
                steerable.setMaxLinearSpeed(1.5f);
                steerable.setMaxLinearAcceleration(8f);
                enemies.add(new EnemyAI(steerable, playerSteerable));
            }
        }

        engine.addSystem(new DialogInteractSystem(p,dialogBox,dialogManager));
        engine.addSystem(new InteractSystem(p));
        engine.addSystem(new PortalSystem(this,p));

        staticBodyBuilder.createStaticBody();
        portalFactory.buildPortals();

    }

    public void changeMap(String map, String spawnId)
    {
        pendingMap = map;
        pendingSpawn = spawnId;
    }

    private void changeMapNow()
    {
        String mapPath = "";
        if("cave".equals(pendingMap))
        {
            mapPath = "TiledProject/maps/cave.tmx";
        }
        else if("town".equals(pendingMap))
        {
            mapPath = "TiledProject/maps/town.tmx";
        }
        String spawn = pendingSpawn;
        pendingMap = null;

        Screen old = main.getScreen();
        main.setScreen(new GameScreen(assetManager, main, mapPath, spawn));
        old.dispose();

        pendingSpawn = null;
        pendingMap = null;
    }



    @Override
    public void render(float delta) {
        ScreenUtils.clear(0,0,0,1);
        engine.update(delta);
        for (EnemyAI e : enemies) {
            e.update(delta);
        }

        if(pendingSpawn != null)
        {
            changeMapNow();
        }
    }

    private void setDialog()
    {
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        mainTable.add(dialogBox).growX().height(100).pad(10);
        mainTable.bottom();

        dialogStage.addActor(mainTable);
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
