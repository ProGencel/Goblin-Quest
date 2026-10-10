package com.kitswiki.gquest.ai;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
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

    public final Array<Vector2> patrolPoints = new Array<>();
    public float patrolWait = 4f;
    public float patrolReach;
    private int patrolIndex;
    private boolean patrolWaiting;
    private float patrolWaitTimer;
    private float patrolRepathTimer;

    public EnemyAI(SteerableBody steerable, SteerableBody target, World world, TileGraph graph) {
        this.steerable = steerable;
        this.target = target;
        this.seek = new Seek<>(steerable, target);
        this.world = world;
        this.graph = graph;

        this.stateMachine = new DefaultStateMachine<>(this);
        this.stateMachine.changeState(EnemyState.PATROL);
        this.seekWayPoint = new Seek<>(steerable, wayPoint);

        this.waypointReach = graph.tileSize * 0.4f;
        this.patrolReach = graph.tileSize * 0.4f;
    }

    public DefaultGraphPath<TileNode> getPath() { return path; }

    public void startChase() {
        path.clear();
        repathTimer = 0f;
    }

    private void followPath(Vector2 finalPos) {
        int count = path.getCount();
        while (pathIndex < count) {
            graph.nodeCenter(path.get(pathIndex), tmp2);
            if (steerable.getPosition().dst(tmp2) > waypointReach) break;
            pathIndex++;
        }

        if (pathIndex < count) graph.nodeCenter(path.get(pathIndex), wayPoint.position);
        else wayPoint.position.set(finalPos);

        steerable.setBehavior(seekWayPoint);
    }
    //TODO disaridaki duvarlarida sayiyor grapha onlari devre disi birak veya pathfndingde oralari sectirme

    public boolean hasPatrol()
    {
        return patrolPoints.size > 0;
    }

    public void startPatrol()
    {
        path.clear();
        patrolRepathTimer = 0f;
        patrolWaiting = false;

        float best = Float.MAX_VALUE;
        for(int i = 0; i < patrolPoints.size; i++)
        {
            float d = steerable.getPosition().dst2(patrolPoints.get(i));
            if(d < best)
            {
                best = d;
                patrolIndex = i;
            }
        }
    }

    public void updatePatrol()
    {
        if(!hasPatrol())
        {
            steerable.stop();
            return;
        }

        Vector2 goal = patrolPoints.get(patrolIndex);

        if(patrolWaiting)
        {
            steerable.stop();
            patrolWaitTimer -= dt;
            if(patrolWaitTimer <= 0f)
            {
                patrolWaiting = false;
                patrolIndex = (patrolIndex + 1) % patrolPoints.size;
                path.clear();
                patrolRepathTimer = 0f;
            }
            return;
        }

        if(steerable.getPosition().dst(goal) <= patrolReach)
        {
            steerable.stop();
            patrolWaiting = true;
            patrolWaitTimer = patrolWait;
            path.clear();
            return;
        }

        patrolRepathTimer -= dt;
        if(path.getCount() == 0 || patrolRepathTimer <= 0)
        {
            patrolRepathTimer = 1.5f;
            if(!graph.findPath(steerable.getPosition(), goal,path))
            {
                patrolIndex = (patrolIndex + 1) % patrolPoints.size;
                path.clear();
                return;
            }
            pathIndex = path.getCount() > 1 ? 1 : 0;
        }

        followPath(goal);
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

        followPath(target.getPosition());
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

    public boolean isPatrolWaiting() {
        return patrolWaiting;
    }

    public int getPatrolIndex() {
        return patrolIndex;
    }
}
