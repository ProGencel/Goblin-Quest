package com.kitswiki.gquest.ai;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

public enum EnemyState implements State<EnemyAI> {


    IDLE {
        @Override
        public void enter(EnemyAI entity) {System.out.println("IDLE");
            entity.steerable.stop();
        }

        @Override
        public void update(EnemyAI entity) {

            if(entity.distaneToTarget() < entity.detectRange)
            {
                entity.stateMachine.changeState(CHASE);
            }
        }
    },

    CHASE {
        @Override
        public void enter(EnemyAI entity) {System.out.println("CHASE");
            entity.steerable.setBehavior(entity.seek);
        }

        @Override
        public void update(EnemyAI entity) {

            if(entity.distaneToTarget() < entity.attackRange)
            {
                entity.stateMachine.changeState(ATTACK);
            }
            else if(entity.distaneToTarget() > entity.loseRange)
            {
                entity.stateMachine.changeState(IDLE);
            }
        }
    },

    ATTACK {
        @Override
        public void enter(EnemyAI entity) {System.out.println("ATTACK");
            entity.steerable.stop();
        }

        @Override
        public void update(EnemyAI entity) {

            float dis = entity.distaneToTarget();
            if(dis > entity.attackRange)
            {
                entity.stateMachine.changeState(CHASE);
            }
        }
    };

    @Override
    public void enter(EnemyAI entity) {}

    @Override
    public void update(EnemyAI entity) {}

    @Override
    public void exit(EnemyAI entity) {}

    @Override
    public boolean onMessage(EnemyAI entity, Telegram telegram) {return false;}
}
