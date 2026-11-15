package com.kitswiki.gquest.components;

import com.badlogic.ashley.core.Component;
import com.kitswiki.gquest.ai.enemy.EnemyState;
import com.kitswiki.gquest.ai.skeleton.SkeletonState;
import com.kitswiki.gquest.enums.CharacterState;

public class StateComponent implements Component {

    public CharacterState charState = CharacterState.IDLE;
    public float stateTime = 0f;

    public void set(CharacterState newState)
    {
        if(newState != null)
        {
            charState = newState;
            stateTime = 0f;
        }
    }

    public void set(EnemyState enemyState)
    {
        CharacterState pastState = null;
        switch (enemyState) {
            case IDLE -> pastState = CharacterState.IDLE;
            case CHASE, PATROL -> pastState = CharacterState.RUN;
            case ATTACK -> pastState = CharacterState.ATTACK;
        }

        if(pastState != charState)
        {
            charState = pastState;
        }
    }

    public void set(SkeletonState skeletonState)
    {
        CharacterState pastState = null;
        switch (skeletonState) {
            case IDLE, HIDING -> pastState = CharacterState.IDLE;
            case GOTO_CAVE, GOTO_START  -> pastState = CharacterState.RUN;
        }

        if(pastState != charState)
        {
            charState = pastState;
        }
    }

}
