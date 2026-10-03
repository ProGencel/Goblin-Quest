package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;
import com.kitswiki.gquest.components.AnimationComponent;
import com.kitswiki.gquest.components.StateComponent;
import com.kitswiki.gquest.components.TextureComponent;

public class AnimationSystem extends IteratingSystem {

    private final ComponentMapper<AnimationComponent> ac = ComponentMapper.getFor(AnimationComponent.class);
    private final ComponentMapper<TextureComponent> tc = ComponentMapper.getFor(TextureComponent.class);
    private final ComponentMapper<StateComponent> sc = ComponentMapper.getFor(StateComponent.class);
    private final TextureAtlas atlas;
    private final ObjectMap<String, Animation<TextureRegion>> cache;

    public AnimationSystem(TextureAtlas atlas) {
        super(Family.all(StateComponent.class, TextureComponent.class, AnimationComponent.class).get());
        this.cache = new ObjectMap<>();
        this.atlas = atlas;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent st = sc.get(entity);
        TextureComponent tex = tc.get(entity);
        AnimationComponent anim = ac.get(entity);

        st.stateTime += deltaTime;
        tex.regions.clear();
        for(String layer : anim.layers)
        {
            tex.regions.add(getAnimation(layer,st).getKeyFrame(st.stateTime));
        }
    }

    private Animation<TextureRegion> getAnimation(String layer, StateComponent st)
    {
        String key = layer + "_" + st.charState.name().toLowerCase();
        Animation<TextureRegion> a = cache.get(key);
        if(a == null)
        {
            TextureRegion strip = atlas.findRegion(key);
            TextureRegion[] frames = strip.split(96,64)[0];
            a = new Animation<>(0.12f, frames);
            cache.put(key, a);
        }

        return a;

    }
}
