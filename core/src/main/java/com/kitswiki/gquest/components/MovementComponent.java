package com.kitswiki.gquest.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class MovementComponent implements Component {

    public Vector2 dir = new Vector2(0,0);
    public float speed = 5f;

}
