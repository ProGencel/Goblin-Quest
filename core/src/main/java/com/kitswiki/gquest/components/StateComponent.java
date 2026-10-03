package com.kitswiki.gquest.components;

import com.badlogic.ashley.core.Component;
import com.kitswiki.gquest.enums.CharacterState;
import com.kitswiki.gquest.enums.Direction;

public class StateComponent implements Component {

    public CharacterState charState = CharacterState.IDLE;
    public Direction charDirection = Direction.RIGHT;
    public float stateTime = 0f;

    public void set(CharacterState newState)
    {
        if(newState != null)
        {
            charState = newState;
            stateTime = 0f;
        }
    }

}
