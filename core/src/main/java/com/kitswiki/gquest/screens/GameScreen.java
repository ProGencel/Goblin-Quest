package com.kitswiki.gquest.screens;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kitswiki.gquest.GameSession;
import com.kitswiki.gquest.Main;
import com.kitswiki.gquest.ai.skeleton.SkeletonState;
import com.kitswiki.gquest.components.SkeletonAIComponent;
import com.kitswiki.gquest.effects.BlackScreen;
import com.kitswiki.gquest.enums.BACK_MUSIC;
import com.kitswiki.gquest.factories.CloudFactory;
import com.kitswiki.gquest.factories.LightFactory;
import com.kitswiki.gquest.hud.*;
import com.kitswiki.gquest.ai.pathfinding.TileGraph;
import com.kitswiki.gquest.components.EnemyAIComponent;
import com.kitswiki.gquest.components.InventoryComponent;
import com.kitswiki.gquest.debug.AIDebugRenderer;
import com.kitswiki.gquest.debug.DebugPhsic;
import com.kitswiki.gquest.effects.ScreenFade;
import com.kitswiki.gquest.factories.EntityFactory;
import com.kitswiki.gquest.factories.PortalFactory;
import com.kitswiki.gquest.interfaces.ChangeMap;
import com.kitswiki.gquest.listeners.GameContactListener;
import com.kitswiki.gquest.managers.AudioManager;
import com.kitswiki.gquest.map.MapObjectBuilder;
import com.kitswiki.gquest.map.StaticBodyBuilder;
import com.kitswiki.gquest.map.TiledMapReader;
import com.kitswiki.gquest.systems.*;

import static com.kitswiki.gquest.utils.Constants.UNIT_SCALE;

public class GameScreen implements Screen, ChangeMap {

    private final Main main;
    private final GameSession gameSession;

    private final DelayedSpawnSystem delayedSpawnSystem;
    private final DayNightSystem dayNightSystem;

    private final AssetManager assetManager;
    private final TiledMap tiledMap;
    private final SpriteBatch batch;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final TiledMapReader mapReader;
    private final World world;
    private final RayHandler rayHandler;

    private final Viewport uiViewport;
    private final Stage dialogStage;
    private final DialogManager dialogManager;
    private final ShopManager shopManager;
    private final GameState gameState;
    private TileGraph graph;

    private final Engine engine;
    private final EntityFactory entityFactory;
    private final PortalFactory portalFactory;
    private final LightFactory lightFactory;
    private final MapObjectBuilder mapObjectBuilder;
    private final StaticBodyBuilder staticBodyBuilder;
    private final Hud hud;
    private final AudioManager audioManager;
    private final PauseMenu pauseMenu;

    private final Vector2 spawnPos;

    private String pendingMap;
    private String pendingSpawn;
    private String currMap;
    private AudioManager.TYPE musicType;

    private final ScreenFade fade;
    private final BlackScreen blackScreen;

    private ImmutableArray<Entity> aiEntities;
    private ImmutableArray<Entity> skeEntities;
    private final AIDebugRenderer aiDebugRenderer;
    private final DebugPhsic debugPhsic;

    private Entity p;
    private final InventoryComponent i;
    private boolean isPaused = false;

    private Runnable pendingScreenChange;


    public GameScreen(GameSession gameSession, String mapPath, String spawnId,
                      boolean startFadedIn, BACK_MUSIC backMusic) {
        this.main = gameSession.getMain();
        this.gameSession = gameSession;

        this.assetManager = gameSession.getAssetManager();
        this.tiledMap = assetManager.get(mapPath);
        this.currMap = mapPath.contains("town") ? "town" : "cave";
        this.batch = new SpriteBatch();
        this.engine = new PooledEngine();
        this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, UNIT_SCALE,batch);
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(480f * UNIT_SCALE, 270f * UNIT_SCALE,camera);
        this.world = new World(new Vector2(0,0),true);
        this.rayHandler = new RayHandler(world);
        this.rayHandler.setDiffuseLight(true);
        this.rayHandler.setShadows(true);

        this.mapReader = new TiledMapReader(tiledMap, UNIT_SCALE);

        this.uiViewport = new FitViewport(480,270);
        this.dialogManager = gameSession.getDialogManager();
        this.shopManager = gameSession.getShopManager();
        this.dialogStage = new Stage(uiViewport,batch);
        this.gameState = gameSession.getGameState();

        this.delayedSpawnSystem = new DelayedSpawnSystem(gameSession.getGameState());

        this.portalFactory = new PortalFactory(mapReader,engine,world);
        this.lightFactory = new LightFactory(mapReader,rayHandler,engine);
        this.staticBodyBuilder = new StaticBodyBuilder(world,mapReader,engine);
        this.audioManager = gameSession.getAudioManager();
        this.entityFactory = new EntityFactory(world,mapReader,engine,audioManager);
        this.mapObjectBuilder = new MapObjectBuilder(delayedSpawnSystem,mapReader,engine,entityFactory,new CloudFactory(engine,gameSession,mapReader,currMap));
        this.dayNightSystem = new DayNightSystem(gameSession.getDay(),gameSession.getHour(),rayHandler,mapObjectBuilder.getCreateCloud());
        this.hud = new Hud(dialogStage,assetManager,dayNightSystem,gameSession);
        hud.setDialog();

        this.fade = new ScreenFade(startFadedIn);
        this.blackScreen = new BlackScreen();

        this.aiDebugRenderer = new AIDebugRenderer(1f);
        this.debugPhsic = new DebugPhsic(camera,engine,world);
        this.i = gameSession.getInv();
        this.pauseMenu = new PauseMenu(assetManager.get("UI/ui/skin/skin.json"),batch,gameSession.getBundle(),
            () -> {
                isPaused = !isPaused;
                setPaused();
            },() ->{
            audioManager.stopMusic();
            main.setScreen(new MainMenuScreen(gameSession,main));
        });

        spawnPos = mapReader.getPointByProperty("spawns","spawnId",spawnId).getPosition();
        aiEntities = engine.getEntitiesFor(Family.all(EnemyAIComponent.class).get());
        skeEntities = engine.getEntitiesFor(Family.all(SkeletonAIComponent.class).get());

        switch (backMusic)
        {
            case TOWN -> {
                musicType = AudioManager.TYPE.TOWN_THEME;
                audioManager.playMusic(AudioManager.TYPE.TOWN_THEME);
            }
            case CAVE ->
                {
                    musicType = AudioManager.TYPE.CAVE;
                    audioManager.playMusic(AudioManager.TYPE.CAVE);
                }
        }
    }

    @Override
    public void show() {

        staticBodyBuilder.createStaticBody();
        portalFactory.buildPortals();
        mapObjectBuilder.loadStaticObjects();

        graph = new TileGraph(world, tiledMap);
        entityFactory.setGraph(graph);

        p = entityFactory.createPlayer(spawnPos,i,gameSession.getHealthC());
        lightFactory.createLights();
        engine.addEntity(p);

        camera.position.set(spawnPos,0);
        world.setContactListener(new GameContactListener());

        mapObjectBuilder.setP(p);
        mapObjectBuilder.loadDynamicObjects(gameState);
        Skin skin = assetManager.get("UI/ui/skin/skin.json");
        engine.addSystem(dayNightSystem);
        engine.addSystem(new InputSystem());
        engine.addSystem(new AISystem());
        engine.addSystem(new SkeletonAISystem());
        engine.addSystem(new MovementSystem(mapReader,audioManager));
        engine.addSystem(new PhysicSystem(world));
        engine.addSystem(new PhysicSyncSystem());
        engine.addSystem(new DamageSystem(p));
        engine.addSystem(new DeathSystem(this,"town","begin",gameSession,audioManager));
        engine.addSystem(delayedSpawnSystem);
        engine.addSystem(new InteractSystem(p));
        engine.addSystem(new DialogInteractSystem(p, hud.getDialogBox(), gameState,
            () -> pendingScreenChange = this::showEnding,gameSession));
        engine.addSystem(new PortalSystem(this,p));
        engine.addSystem(new CollectableInteractSystem(gameState));
        engine.addSystem(new CameraSystem(camera,mapReader,viewport));
        engine.addSystem(new AnimationSystem(assetManager.get("atlas/cooked/gquest.atlas")));
        engine.addSystem(new RoofSystem());
        engine.addSystem(new RenderSystem(batch,tiledMap,camera,mapRenderer,dialogStage,this,viewport,rayHandler,gameSession,mapReader));
        engine.addSystem(new CloudSystem());
        engine.addSystem(new LightSystem());
        engine.addSystem(new InGameUiSystem(assetManager,p));

    }

    public void changeMap(String map, String spawnId)
    {
        if(pendingMap != null)
        {
            return;
        }
        if ("cave".equals(map) && isCaveBusy()) {
            if (!hud.getDialogBox().isOpen()) {
                hud.getDialogBox().start(
                    new String[]{gameSession.getBundle().get("cave.busy")}, null
                );
            }
            return;
        }
        pendingMap = map;
        pendingSpawn = spawnId;
        fade.fadeOut();
    }

    private void changeMapNow(boolean startFadedIn) {
        BACK_MUSIC back = BACK_MUSIC.TOWN;
        if ("cave".equals(pendingMap))
        {
            gameState.set("goblin.cave_entry");
            back = BACK_MUSIC.CAVE;
        }
        gameSession.setDay(dayNightSystem.getDay());
        gameSession.setHour(dayNightSystem.getHour());
        String mapPath = "TiledProject/maps/" + pendingMap + ".tmx";
        audioManager.stopMusic();
        main.setScreen(new GameScreen(gameSession,mapPath,pendingSpawn,startFadedIn,back));
    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, 1/30f);

        ScreenUtils.clear(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
        {
            isPaused = !isPaused;
            setPaused();
            pauseMenu.setActive(isPaused);
        }

        engine.update(delta);
        if(pendingScreenChange != null)
        {
            pendingScreenChange.run();
            return;
        }
        fade.update(delta);
        fade.draw();

        if(pendingMap != null && fade.isFadedOut())
        {
            changeMapNow(true);
            return;
        }

        aiDebugRenderer.update(camera,aiEntities,skeEntities,graph);
        debugPhsic.update();

        if("cave".equals(currMap))
        {
            blackScreen.draw();
        }

        batch.setProjectionMatrix(uiViewport.getCamera().combined);

        batch.begin();
        new BitmapFont().draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 20); //MEMORY LEAK SAKIN ACIK UNUTMA !!!!
        batch.end();

        audioManager.checkVolume();
        pauseMenu.render();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        uiViewport.update(width, height, true);
        pauseMenu.resize(width, height);
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

    public boolean isOutside()
    {
        return "town".equals(currMap);
    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
        aiDebugRenderer.dispose();
        rayHandler.dispose();
        world.dispose();
        //batch.dispose();
        dialogStage.dispose();
        fade.dispose();
    }

    private boolean isCaveBusy() {
        for (Entity e : skeEntities) {
            SkeletonAIComponent ai = e.getComponent(SkeletonAIComponent.class);

            if (ai.skeletonAI.getStateMachine().isInState(SkeletonState.HIDING) || ai.skeletonAI.getStateMachine().isInState(SkeletonState.GOTO_CAVE)) {
                return true;
            }
        }
        return false;
    }

    private void showEnding() {
        Skin skin = assetManager.get("UI/ui/skin/skin.json");
        BitmapFont font = skin.getFont("Minecraft_2");
        main.setScreen(new EndingScreen(font,
            () -> main.setScreen(new CreditScreen(font,
                () -> main.setScreen(new MainMenuScreen(gameSession, main)),
                gameSession.getBundle(),gameSession)),
            gameSession.getBundle(),gameSession));
    }

    private void setPaused()
    {
        for(EntitySystem system : engine.getSystems())
        {
            if(!(system instanceof RenderSystem) && !(system instanceof InGameUiSystem))
            {
                system.setProcessing(!isPaused);
            }
        }
    }
}
