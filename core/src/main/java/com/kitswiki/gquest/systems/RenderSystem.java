package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.kitswiki.gquest.components.TextureComponent;
import com.kitswiki.gquest.components.TransformComponent;
import com.kitswiki.gquest.utils.Constants;

import java.util.Comparator;

import static com.kitswiki.gquest.utils.Constants.UNIT_SCALE;

public class RenderSystem extends SortedIteratingSystem {

    private final ComponentMapper<TextureComponent> tc = ComponentMapper.getFor(TextureComponent.class);
    private final ComponentMapper<TransformComponent> tsc = ComponentMapper.getFor(TransformComponent.class);
    private final ShapeRenderer shape = new ShapeRenderer();
    private final YComparator debugComparator = new YComparator();
    private final SpriteBatch batch;
    private final Box2DDebugRenderer debugRenderer;
    private final TiledMap map;
    private final OrthographicCamera camera;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final World world;

    private final int aboveIndex;
    private final MapLayer aboveLayer;

    public RenderSystem(SpriteBatch batch, TiledMap map, OrthographicCamera camera, OrthogonalTiledMapRenderer mapRenderer, World world) {
        super(Family.all(TransformComponent.class, TextureComponent.class).get(),new YComparator());
        this.debugRenderer = new Box2DDebugRenderer();
        this.batch = batch;
        this.map = map;
        this.camera = camera;
        this.mapRenderer = mapRenderer;
        this.world = world;

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

        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        super.update(deltaTime);
        batch.end();

        if(aboveLayer != null)
        {
            aboveLayer.setVisible(true);
            mapRenderer.render(new int[] {aboveIndex});
            aboveLayer.setVisible(false);
        }

        //debugDrawSortLines();
        //debugRenderer.render(world,camera.combined);
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

    private void debugDrawSortLines() {
        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Line);
        for (Entity e : getEntities()) {
            TransformComponent t = tsc.get(e);
            float y = debugComparator.sortY(e);
            shape.setColor(Color.RED);
            shape.line(t.position.x - 0.5f, y, t.position.x + 0.5f, y);
            shape.setColor(Color.GREEN);
            shape.circle(t.position.x, t.position.y, 0.05f, 8);
        }
        shape.end();
    }

    public static class YComparator implements Comparator<Entity>
    {
        private final ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
        private final ComponentMapper<TextureComponent> tc = ComponentMapper.getFor(TextureComponent.class);

        private float sortY(Entity e) {
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

