package com.kitswiki.gquest.systems;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kitswiki.gquest.GameSession;
import com.kitswiki.gquest.components.*;
import com.kitswiki.gquest.map.TiledMapReader;
import com.kitswiki.gquest.screens.GameScreen;

import java.util.Comparator;

import static com.kitswiki.gquest.utils.Constants.UNIT_SCALE;

public class RenderSystem extends SortedIteratingSystem {

    private final ComponentMapper<TextureComponent> tc = ComponentMapper.getFor(TextureComponent.class);
    private final ComponentMapper<TransformComponent> tsc = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<RoofComponent> r = ComponentMapper.getFor(RoofComponent.class);
    private final ComponentMapper<CloudComponent> c = ComponentMapper.getFor(CloudComponent.class);

    private final Family roofFamily = Family.all(RoofComponent.class).get();
    private final Family cloudFamily = Family.all(CloudComponent.class).get();

    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Stage dialogStage;
    private final GameScreen currScreen;
    private final RayHandler rayHandler;
    private final TiledMapReader mapReader;

    private final Array<Rectangle> maskRecs = new Array<>();
    private final TextureRegion whitePixel;

    private final int aboveIndex;
    private final MapLayer aboveLayer;

    public RenderSystem(SpriteBatch batch, TiledMap map, OrthographicCamera camera, OrthogonalTiledMapRenderer mapRenderer, Stage dialogStage,
                        GameScreen screen, Viewport viewport, RayHandler rayHandler, GameSession gameSession, TiledMapReader mapReader) {
        super(Family.all(TransformComponent.class, TextureComponent.class).exclude(RoofComponent.class, HiddenComponent.class).get(),new YComparator());
        this.batch = batch;
        this.camera = camera;
//        camera.zoom = 5f;
        this.mapRenderer = mapRenderer;
        this.dialogStage = dialogStage;
        this.currScreen = screen;
        this.viewport = viewport;
        this.rayHandler = rayHandler;
        Skin skin = gameSession.getAssetManager().get("UI/ui/skin/skin.json");
        this.whitePixel = skin.getRegion("white-pixel");
        this.mapReader = mapReader;
        aboveLayer = map.getLayers().get("above");
        aboveIndex = map.getLayers().getIndex("above");
        if(aboveLayer != null)
        {
            aboveLayer.setVisible(false);
        }
    }

    @Override
    public void update(float deltaTime) {
        forceSort();

        viewport.apply();
        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        super.update(deltaTime);
        drawRoofs();
        batch.end();

        if(aboveLayer != null)
        {
            aboveLayer.setVisible(true);
            mapRenderer.render(new int[] {aboveIndex});
            aboveLayer.setVisible(false);
        }

        getEngine().getSystem(DayNightSystem.class).updateAmbinetLight(currScreen.isOutside());


        rayHandler.setCombinedMatrix(camera);
        rayHandler.useCustomViewport(
            viewport.getScreenX(),
            viewport.getScreenY(),
            viewport.getScreenWidth(),
            viewport.getScreenHeight()
        );
        rayHandler.updateAndRender();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        fillRoofRecs();
        drawClouds();
        batch.end();

        dialogStage.act();
        dialogStage.draw();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Vector2 pos = tsc.get(entity).position;
        TextureComponent tc = this.tc.get(entity);

        for(TextureRegion region : this.tc.get(entity).regions)
        {
            float w = region.getRegionWidth() * UNIT_SCALE;
            float h = region.getRegionHeight() * UNIT_SCALE;
            float x = pos.x - w/2;
            float y = pos.y - h/2;
            if(entity.getComponent(PlayerComponent.class) == null && entity.getComponent(SkeletonAIComponent.class) == null && entity.getComponent(EnemyAIComponent.class) == null)
            {
                x = Math.round(x * (1/UNIT_SCALE)) / (1/UNIT_SCALE);
                y = Math.round(y * (1/UNIT_SCALE)) / (1/UNIT_SCALE);
            }

            if(!tc.flipped)
            {
                batch.draw(region,x,y,w,h);
            }
            else
            {
                batch.draw(region,x + w,y,-w,h);
            }
        }
    }

    private void drawClouds()
    {
        Color ambient = getEngine().getSystem(DayNightSystem.class).getAmbient();
        float lum = 0.299f * ambient.r
            + 0.587f * ambient.g
            + 0.114f * ambient.b;

        float t = MathUtils.clamp(
            (lum - 0.45f) / (0.60f - 0.45f), 0f, 1f
        );

        float alpha = Interpolation.smooth.apply(t);

        if(alpha <= 0.01f) return;

        boolean masked = maskRecs.size > 0;
        if(masked) writeMask();

        float oldColor = batch.getPackedColor();
        batch.setColor(1f, 1f, 1f, alpha);

        for(Entity e : getEngine().getEntitiesFor(cloudFamily))
        {
            CloudComponent c = this.c.get(e);
            TransformComponent transform = this.tsc.get(e);

            batch.draw(c.region,
                transform.position.x,
                transform.position.y,
                c.region.getRegionWidth() * UNIT_SCALE,
                c.region.getRegionHeight() * UNIT_SCALE);
        }

        batch.setPackedColor(oldColor);

        if(masked)
        {
            batch.flush();
            Gdx.gl.glDisable(GL20.GL_STENCIL_TEST);
        }
    }

    private void writeMask()
    {
        batch.flush();

        Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);
        Gdx.gl.glStencilMask(0xFF);
        Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, 1, 0xFF);
        Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE);
        Gdx.gl.glColorMask(false, false, false, false);

        for(Rectangle rect : maskRecs)
        {
            batch.draw(whitePixel, rect.x, rect.y, rect.width, rect.height);
        }
        batch.flush();

        Gdx.gl.glColorMask(true, true, true, true);
        Gdx.gl.glStencilFunc(GL20.GL_EQUAL, 0, 0xFF);
        Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_KEEP);
    }

    private void fillRoofRecs()
    {
        maskRecs.clear();
        for(Entity entity : getEngine().getEntitiesFor(roofFamily))
        {
            RoofComponent roof = this.r.get(entity);
            if(roof.alpha < 1f)
            {
                maskRecs.addAll(mapReader.getRects("not_cloud_recs"));
            }
        }
    }

    private void drawRoofs()
    {
        for(Entity entity : getEngine().getEntitiesFor(roofFamily))
        {
            RoofComponent r = this.r.get(entity);
            if(r.alpha <= 0)
            {
                continue;
            }

            Vector2 pos = tsc.get(entity).position;
            TextureComponent tc = this.tc.get(entity);

            for(TextureRegion region : this.tc.get(entity).regions)
            {
                float w = region.getRegionWidth() * UNIT_SCALE;
                float h = region.getRegionHeight() * UNIT_SCALE;
                float x = pos.x - w/2;
                float y = pos.y - h/2;

                batch.setColor(1f,1f,1f,r.alpha);
                if(!tc.flipped)
                {
                    batch.draw(region,x,y,w,h);
                }
                else
                {
                    batch.draw(region,x + w,y,-w,h);
                }
            }
            batch.setColor(Color.WHITE);
        }
    }

    public static class YComparator implements Comparator<Entity>
    {
        private final ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
        private final ComponentMapper<TextureComponent> tc = ComponentMapper.getFor(TextureComponent.class);

        public float sortY(Entity e) {
            TransformComponent t = tm.get(e);
            TextureComponent s = tc.get(e);
            return t.position.y + s.offsetY;
        }

        @Override
        public int compare(Entity o1, Entity o2) {
            return Float.compare(sortY(o2), sortY(o1));
        }
    }
}

