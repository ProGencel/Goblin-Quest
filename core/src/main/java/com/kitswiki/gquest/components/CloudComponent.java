package com.kitswiki.gquest.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CloudComponent implements Component {

    public TextureRegion region;
    public float speed = 0.5f;
    public int direction = 1;
}
