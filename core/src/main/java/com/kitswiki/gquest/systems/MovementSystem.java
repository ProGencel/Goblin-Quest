package com.kitswiki.gquest.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.kitswiki.gquest.components.BodyComponent;
import com.kitswiki.gquest.components.MovementComponent;
import com.kitswiki.gquest.components.StateComponent;
import com.kitswiki.gquest.components.TextureComponent;
import com.kitswiki.gquest.enums.CharacterState;
import com.kitswiki.gquest.map.TiledMapReader;

import static com.kitswiki.gquest.utils.Constants.UNIT_SCALE;

public class MovementSystem extends IteratingSystem {

    private final ComponentMapper<BodyComponent> b = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<MovementComponent> m = ComponentMapper.getFor(MovementComponent.class);
    private final ComponentMapper<TextureComponent> t = ComponentMapper.getFor(TextureComponent.class);
    private final ComponentMapper<StateComponent> s = ComponentMapper.getFor(StateComponent.class);

    private final TiledMapReader mapReader;

    public MovementSystem(TiledMapReader mapReader) {
        super(Family.all(BodyComponent.class, MovementComponent.class, TextureComponent.class).get());
        this.mapReader = mapReader;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BodyComponent b = this.b.get(entity);
        MovementComponent m = this.m.get(entity);
        TextureComponent t = this.t.get(entity);
        StateComponent s = this.s.get(entity);

        if(!m.dir.equals(new Vector2(0,0)))
        {
            s.charState = CharacterState.RUN;
        }
        else
        {
            s.charState = CharacterState.IDLE;
        }
        if(m.dir.x < 0)
        {
            t.flipped = true;
        }
        else if(m.dir.x > 0)
        {
            t.flipped = false;
        }

        //IF PLAYER SPRITE CHANGED THIS CODE HAS TO CHANGE MANUALLY
        float textureWidth = 8 * UNIT_SCALE;
        float textureHeight = 8 * UNIT_SCALE;

        if(b.body.getPosition().x + textureWidth > mapReader.getWidthWorld() && m.dir.x > 0)
        {
            m.dir.x = 0;
        }
        if(b.body.getPosition().y + textureHeight > mapReader.getHeightWorld() && m.dir.y > 0)
        {
            m.dir.y = 0;
        }

        b.body.setLinearVelocity(m.dir.x * m.speed, m.dir.y * m.speed);
    }
}
