package com.kitswiki.gquest.ai.skeleton;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

public enum SkeletonState implements State<SkeletonAI> {

    IDLE {
        @Override
        public void enter(SkeletonAI entity) {
            entity.getSteerableBody().stop();
        }

        @Override
        public void update(SkeletonAI entity) {
            entity.getSteerableBody().stop();
        }
    },

    GOTO_CAVE {
        @Override
        public void update(SkeletonAI entity) {
            if(entity.nav.moveTo(entity.cave, entity.delta)) {
                entity.getStateMachine().changeState(HIDING);
            }
        }
    },

    HIDING {
        @Override
        public void enter(SkeletonAI entity) {
            entity.getSteerableBody().stop();
            entity.hideTimer = 0f;
        }

        @Override
        public void update(SkeletonAI entity) {
            entity.hideTimer += entity.delta;
            if(entity.hideTimer >= entity.hideTime)
            {
                entity.getStateMachine().changeState(GOTO_START);
            }
        }
    },

    GOTO_START {
        @Override
        public void update(SkeletonAI entity) {
            if(entity.nav.moveTo(entity.start, entity.delta))
            {
                entity.getStateMachine().changeState(IDLE);
            }
        }
    };

    @Override
    public void enter(SkeletonAI entity) {

    }

    @Override
    public void update(SkeletonAI entity) {

    }

    @Override
    public void exit(SkeletonAI entity) {

    }

    @Override
    public boolean onMessage(SkeletonAI entity, Telegram telegram) {
        return false;
    }
}
