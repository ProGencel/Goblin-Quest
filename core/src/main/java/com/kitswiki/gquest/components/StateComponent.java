package com.kitswiki.gquest.components;

import com.badlogic.ashley.core.Component;
import com.kitswiki.gquest.ai.EnemyState;
import com.kitswiki.gquest.enums.CharacterState;
import com.kitswiki.gquest.enums.Direction;

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

}
