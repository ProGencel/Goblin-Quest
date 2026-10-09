package com.kitswiki.gquest.ai;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class EnemyAI {

    public final SteerableBody steerable;
    public final SteerableBody target;
    public final Seek<Vector2> seek;
    public final StateMachine<EnemyAI, EnemyState> stateMachine;

    private final World world;
    private final Vector2 tmp = new Vector2();

    public float detectRange = 4f;
    public float loseRange = 6f;
    public float attackRange = 0.8f;

    public final Vector2 facing = new Vector2(0, -1);
    public float fovDegrees = 90f;
    public float nearRange = 1.2f;
    public float timeSinceSeen = 0f;
    public float forgetTime = 2f;

    public EnemyAI(SteerableBody steerable, SteerableBody target, World world) {
        this.steerable = steerable;
        this.target = target;
        this.seek = new Seek<>(steerable, target);
        this.world = world;

        this.stateMachine = new DefaultStateMachine<>(this);
        this.stateMachine.changeState(EnemyState.IDLE);
    }

    public boolean canSeeTarget()
    {
        tmp.set(target.getPosition()).sub(steerable.getPosition());
        float dist = tmp.len();

        if(dist > detectRange)
        {
            return false;
        }

        if(dist > nearRange)
        {
            tmp.scl(1f / dist);
            float cosHalfFov = MathUtils.cosDeg(fovDegrees / 2f);
            if(facing.dot(tmp) < cosHalfFov)
            {
                return false;
            }
        }

        return hasLineOfSight();
    }

    public boolean hasLineOfSight()
    {
        return LineOfSight.check(world,steerable.getPosition(),target.getPosition());
    }

    private void updateFacing()
    {
        Vector2 vel = steerable.getLinearVelocity();
        if(vel.len2() > 0.04f)
        {
            facing.set(vel).nor();
        }
    }

    public float distaneToTarget()
    {
        return steerable.getPosition().dst(target.getPosition());
    }

    public void update(float dt)
    {
        updateFacing();

        if(hasLineOfSight())
        {
            timeSinceSeen = 0f;
        }
        else
        {
            timeSinceSeen += dt;
        }

        stateMachine.update();
        steerable.update(dt);
    }
}
