package com.kitswiki.gquest.ai.pathfinding;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.kitswiki.gquest.ai.SteerableBody;


public class PathNavigator {

    public final TileGraph graph;
    public final WayPointLocation wayPoint = new WayPointLocation();

    private final SteerableBody steerable;
    private final Seek<Vector2> seekWayPoint;
    private final DefaultGraphPath<TileNode> path = new DefaultGraphPath<>();
    private final Vector2 tmp = new Vector2();
    private final float waypointReach;
    private int pathIndex;

    public final Array<Vector2> patrolPoints = new Array<>();
    public float patrolWait = 4f;
    private final float patrolReach;
    private int patrolIndex;
    private boolean patrolWaiting;
    private float patrolWaitTimer;
    private float patrolRepathTimer;

    private float moveRepathTimer;
    private static final float MOVE_REPATH_INTERVAL = 1.5f;

    public PathNavigator(SteerableBody steerable, TileGraph graph) {
        this.steerable = steerable;
        this.graph = graph;
        this.seekWayPoint = new Seek<>(steerable, wayPoint);
        this.waypointReach = graph.tileSize * 0.4f;
        this.patrolReach = graph.tileSize * 0.4f;
    }

    public DefaultGraphPath<TileNode> getPath() { return path; }

    public void clearPath() { path.clear(); }

    public boolean hasPath() { return path.getCount() > 0; }

    public boolean moveTo(Vector2 goal, float dt)
    {
        if(steerable.getPosition().dst(goal) <= waypointReach)
        {
            steerable.stop();
            clearPath();
            return true;
        }

        moveRepathTimer -= dt;
        if(!hasPath() || moveRepathTimer < 0f)
        {
            moveRepathTimer = MOVE_REPATH_INTERVAL;
            if(!requestPath(goal))
            {
                steerable.stop();
                return false;
            }
            pathIndex = path.getCount() > 1 ? 1 : 0;
        }

        followPath(goal);
        return false;
    }

    public boolean requestPath(Vector2 goal) {
        if (!graph.findPath(steerable.getPosition(), goal, path)) return false;
        pathIndex = 0;
        return true;
    }

    public void followPath(Vector2 finalPos) {
        int count = path.getCount();
        while (pathIndex < count) {
            graph.nodeCenter(path.get(pathIndex), tmp);
            if (steerable.getPosition().dst(tmp) > waypointReach)
            {
                break;
            }
            pathIndex++;
        }

        if (pathIndex < count)
        {
            graph.nodeCenter(path.get(pathIndex), wayPoint.position); //waypoint bir sonraki hedef
        }
        else
        {
            wayPoint.position.set(finalPos); // waypoint bunda zaten
        }

        steerable.setBehavior(seekWayPoint);
    }
    //TODO disaridaki duvarlarida sayiyor grapha onlari devre disi birak veya pathfndingde oralari sectirme

    public boolean hasPatrol() { return patrolPoints.size > 0; }

    public boolean isPatrolWaiting() { return patrolWaiting; }

    public int getPatrolIndex() { return patrolIndex; }

    public void startPatrol() {
        path.clear();
        patrolRepathTimer = 0f;
        patrolWaiting = false;

        float best = Float.MAX_VALUE;
        for (int i = 0; i < patrolPoints.size; i++) {
            float d = steerable.getPosition().dst2(patrolPoints.get(i));
            if (d < best) {
                best = d;
                patrolIndex = i;
            }
        }
    }

    public void updatePatrol(float dt) {
        if (!hasPatrol()) {
            steerable.stop();
            return;
        }

        Vector2 goal = patrolPoints.get(patrolIndex);

        if (patrolWaiting) {
            steerable.stop();
            patrolWaitTimer -= dt;
            if (patrolWaitTimer <= 0f) {
                patrolWaiting = false;
                patrolIndex = (patrolIndex + 1) % patrolPoints.size;
                path.clear();
                patrolRepathTimer = 0f;
            }
            return;
        }

        if (steerable.getPosition().dst(goal) <= patrolReach) {
            steerable.stop();
            patrolWaiting = true;
            patrolWaitTimer = patrolWait;
            path.clear();
            return;
        }

        patrolRepathTimer -= dt;
        if (!hasPath() || patrolRepathTimer <= 0f) {
            patrolRepathTimer = 1.5f;
            if (!requestPath(goal)) {
                patrolIndex = (patrolIndex + 1) % patrolPoints.size;
                path.clear();
                return;
            }

            pathIndex = path.getCount() > 1 ? 1 : 0;
        }

        followPath(goal);
    }
}
