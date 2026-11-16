package com.kitswiki.gquest.ai.enemy;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.kitswiki.gquest.ai.LineOfSight;
import com.kitswiki.gquest.ai.SteerableBody;
import com.kitswiki.gquest.ai.pathfinding.PathNavigator;
import com.kitswiki.gquest.ai.pathfinding.TileGraph;

public class EnemyAI {

    public final SteerableBody steerable;
    public final SteerableBody target;
    public final Seek<Vector2> seek;
    private final Arrive<Vector2> arrive;
    public final StateMachine<EnemyAI, EnemyState> stateMachine;
    public final PathNavigator nav;

    private final World world;
    private final Vector2 tmp = new Vector2();

    private float repathTimer;
    private float dt;

    public float detectRange = 6f;
    public float loseRange = 30f;
    public float attackRange = 0.8f;
    public float repathInterval = 0.4f;

    public final Vector2 facing = new Vector2(0, -1);
    public float fovDegrees = 90f;
    public float nearRange = 1.2f;
    public float timeSinceSeen = 0f;
    public float forgetTime = 8f;

    public EnemyAI(SteerableBody steerable, SteerableBody target, World world, TileGraph graph) {
        this.steerable = steerable;
        this.target = target;
        this.world = world;
        this.seek = new Seek<>(steerable, target);
        this.arrive = new Arrive<>(steerable, target)
            .setArrivalTolerance(0.15f)
            .setDecelerationRadius(1.5f)
            .setTimeToTarget(0.1f);

        // nav, changeState'ten ÖNCE oluşmalı: PATROL.enter() nav'a erişecek
        this.nav = new PathNavigator(steerable, graph);

        this.stateMachine = new DefaultStateMachine<>(this);
        this.stateMachine.changeState(EnemyState.PATROL);
    }

    public void startChase() {
        nav.clearPath();
        repathTimer = 0f;
    }

    public void updatePatrol() {
        nav.updatePatrol(dt);
    }

    public void updateChase() {
        if (hasLineOfSight()) {
            steerable.setBehavior(arrive);
            nav.clearPath();
            repathTimer = 0f;
            return;
        }

        repathTimer -= dt;
        if (repathTimer <= 0f || !nav.hasPath()) {
            repathTimer = repathInterval;
            if (!nav.requestPath(target.getPosition())) {
                steerable.stop();
                return;
            }
        }

        nav.followPath(target.getPosition());
    }

    public boolean canSeeTarget() {
        tmp.set(target.getPosition()).sub(steerable.getPosition());
        float dist = tmp.len();

        if (dist > detectRange) return false;

        if (dist > nearRange) {
            tmp.scl(1f / dist);
            float cosHalfFov = MathUtils.cosDeg(fovDegrees / 2f);
            if (facing.dot(tmp) < cosHalfFov) return false;
        }

        return hasLineOfSight();
    }

    public boolean hasLineOfSight() {
        return LineOfSight.check(world, steerable.getPosition(), target.getPosition());
    }

    private void updateFacing() {
        Vector2 vel = steerable.getLinearVelocity();
        if (vel.len2() > 0.04f) {
            facing.set(vel).nor();
        }
    }

    public float distaneToTarget() {
        return steerable.getPosition().dst(target.getPosition());
    }

    public void update(float dt) {
        this.dt = dt;
        updateFacing();

        if (hasLineOfSight()) {
            timeSinceSeen = 0f;
        } else {
            timeSinceSeen += dt;
        }

        stateMachine.update();
        steerable.update(dt);
    }
}
