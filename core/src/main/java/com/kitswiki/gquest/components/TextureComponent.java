package com.kitswiki.gquest.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class TextureComponent implements Component {

    public final Array<TextureRegion> regions = new Array<>();
}
