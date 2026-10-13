package com.kitswiki.gquest.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;


public class AnimationComponent implements Component {

    public final Array<String> layers = new Array<>();
    public Animation<TextureRegion> tileAnim;

}
