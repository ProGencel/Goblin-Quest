package com.kitswiki.gquest.ai;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.kitswiki.gquest.ai.pathfinding.TileGraph;
import com.kitswiki.gquest.ai.pathfinding.TileNode;
import com.kitswiki.gquest.ai.pathfinding.WayPointLocation;

public class EnemyAI {

    public final SteerableBody steerable;
    public final SteerableBody target;
    public final Seek<Vector2> seek;
    public final StateMachine<EnemyAI, EnemyState> stateMachine;
    public final TileGraph graph;
    public final WayPointLocation wayPoint = new WayPointLocation();
    public final Seek<Vector2> seekWayPoint;

    private final World world;
    private final Vector2 tmp = new Vector2();
    private final DefaultGraphPath<TileNode> path = new DefaultGraphPath<>();
    private final Vector2 tmp2 = new Vector2();

    private int pathIndex;
    private float repathTimer;
    private float dt;

    public float detectRange = 4f;
    public float loseRange = 30f;
    public float attackRange = 0.8f;
    public float repathInterval = 0.4f;
    public float waypointReach;

    public final Vector2 facing = new Vector2(0, -1);
    public float fovDegrees = 90f;
    public float nearRange = 1.2f;
    public float timeSinceSeen = 0f;
    public float forgetTime = 8f;

    public EnemyAI(SteerableBody steerable, SteerableBody target, World world, TileGraph graph) {
        this.steerable = steerable;
        this.target = target;
        this.seek = new Seek<>(steerable, target);
        this.world = world;
        this.graph = graph;

        this.stateMachine = new DefaultStateMachine<>(this);
        this.stateMachine.changeState(EnemyState.IDLE);
        this.seekWayPoint = new Seek<>(steerable, wayPoint);

        this.waypointReach = graph.tileSize * 0.4f;
    }

    public DefaultGraphPath<TileNode> getPath() { return path; }

    public void startChase() {
        path.clear();
        repathTimer = 0f;
    }

    public void updateChase() {
        if (hasLineOfSight()) {
            steerable.setBehavior(seek);
            path.clear();
            repathTimer = 0f;
            return;
        }

        repathTimer -= dt;
        if (repathTimer <= 0f || path.getCount() == 0) {
            repathTimer = repathInterval;
            if (!graph.findPath(steerable.getPosition(), target.getPosition(), path)) {
                steerable.stop();
                return;
            }
            pathIndex = 0;
        }

        int count = path.getCount();
        while (pathIndex < count) {
            graph.nodeCenter(path.get(pathIndex), tmp2);
            if (steerable.getPosition().dst(tmp2) > waypointReach) break;
            pathIndex++;
        }

        if (pathIndex < count) graph.nodeCenter(path.get(pathIndex), wayPoint.position);
        else wayPoint.position.set(target.getPosition());

        steerable.setBehavior(seekWayPoint);
    }

    public boolean canSeeTarget()
    {
        tmp.set(target.getPosition()).sub(steerable.getPosition());
        float dist = tmp.len(); //Calculate between target and enemy distance

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
        this.dt = dt;
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
