package com.kitswiki.gquest.components;

import box2dLight.PointLight;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class LightComponent implements Component {

    public PointLight pointLight;
    public Entity targetRoof;

}
