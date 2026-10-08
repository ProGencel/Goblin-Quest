package com.kitswiki.gquest.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class PlayerComponent implements Component {
    public boolean interact = false;
    public Entity interactTarget;
}
