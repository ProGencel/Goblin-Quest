package com.kitswiki.gquest.ai;

import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.math.Vector2;

public class EnemyAI {

    public final SteerableBody steerable;
    public final SteerableBody target;
    public final Seek<Vector2> seek;
    public final StateMachine<EnemyAI, EnemyState> stateMachine;

    public float detectRange = 4f;
    public float loseRange = 6f;
    public float attackRange = 0.8f;

    public EnemyAI(SteerableBody steerable, SteerableBody target, Seek<Vector2> seek, StateMachine<EnemyAI, EnemyState> stateMachine) {
        this.steerable = steerable;
        this.target = target;
        this.seek = new Seek<>(steerable, target);
        this.stateMachine = stateMachine;
    }
}
