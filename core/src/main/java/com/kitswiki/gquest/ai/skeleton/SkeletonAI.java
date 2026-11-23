package com.kitswiki.gquest.ai.skeleton;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.ObjectMap;
import com.kitswiki.gquest.ai.SteerableBody;
import com.kitswiki.gquest.ai.pathfinding.PathNavigator;
import com.kitswiki.gquest.ai.pathfinding.TileGraph;

public class SkeletonAI {

    private final SteerableBody steerableBody;
    private final StateMachine<SkeletonAI, SkeletonState> stateMachine;

    public final PathNavigator nav;
    public Vector2 cave;
    Vector2 start;
    float hideTime;
    float hideTimer;
    float delta;

    public SkeletonAI(SteerableBody steerableBody, ObjectMap<String, Vector2> locs, TileGraph graph, float hideTime) {
        this.steerableBody = steerableBody;
        this.cave = locs.get("cave");
        this.start = locs.get("start");
        this.hideTime = hideTime;
        this.nav = new PathNavigator(steerableBody,graph);
        this.stateMachine = new DefaultStateMachine<>(this, SkeletonState.IDLE);
    }

    public void setPushable(boolean pushable)
    {
        Body body = steerableBody.getBody();
        if(pushable)
        {
            body.setType(BodyDef.BodyType.DynamicBody);
        }
        else
        {
            body.setType(BodyDef.BodyType.StaticBody);
        }
    }

    public void moveToCave()
    {
        stateMachine.changeState(SkeletonState.GOTO_CAVE);
    }

    public boolean isHidden()
    {
        return stateMachine.isInState(SkeletonState.HIDING);
    }

    public void update(float delta)
    {
        this.delta = delta;
        stateMachine.update();
        steerableBody.update(delta);
    }

    public SteerableBody getSteerableBody() {
        return steerableBody;
    }

    public StateMachine<SkeletonAI, SkeletonState> getStateMachine() {
        return stateMachine;
    }
}
