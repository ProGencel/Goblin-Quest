package com.kitswiki.gquest.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

public class ContactComponent implements Component {
    public final Array<Entity> touching = new Array<>();
}
