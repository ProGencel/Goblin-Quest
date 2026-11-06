package com.kitswiki.gquest.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class RoofComponent implements Component {

    public final Array<Rectangle> rectangles = new Array<>();
    public float alpha = 1f;
    public String roof_id;

}
