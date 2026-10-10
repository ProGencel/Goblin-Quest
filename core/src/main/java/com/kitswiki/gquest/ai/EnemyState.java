package com.kitswiki.gquest.ai;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

public enum EnemyState implements State<EnemyAI> {


    IDLE {
        @Override
        public void enter(EnemyAI entity) {
            entity.steerable.stop();
        }

        @Override
        public void update(EnemyAI entity) {

            if(entity.canSeeTarget())
            {
                entity.stateMachine.changeState(CHASE);
                return;
            }
            if(!entity.hasPatrol())
            {
                return;
            }

            entity.updatePatrol();
            if(!entity.isPatrolWaiting())
            {
                entity.stateMachine.changeState(PATROL);
            }
        }
    },

    CHASE {
        @Override
        public void enter(EnemyAI entity) {
            entity.startChase();
        }

        @Override
        public void update(EnemyAI entity) {
            float dist = entity.distaneToTarget();
            if(entity.distaneToTarget() < entity.attackRange)
            {
                entity.stateMachine.changeState(ATTACK);
            }
            else if(dist > entity.loseRange || entity.timeSinceSeen > entity.forgetTime)
            {
                entity.stateMachine.changeState(PATROL);
            }
            else
            {
                entity.updateChase();
            }
        }
    },

    ATTACK {
        @Override
        public void enter(EnemyAI entity) {
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
    },

    PATROL {
        @Override
        public void enter(EnemyAI entity) {
            if(entity.stateMachine.getPreviousState() != IDLE)
            {
                entity.startPatrol();
            }
        }

        @Override
        public void update(EnemyAI entity) {
            if(entity.canSeeTarget())
            {
                entity.stateMachine.changeState(CHASE);
                return;
            }
            if(!entity.hasPatrol())
            {
                entity.stateMachine.changeState(IDLE);
                return;
            }

            entity.updatePatrol();
            if(entity.isPatrolWaiting())
            {
                entity.stateMachine.changeState(IDLE);
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
