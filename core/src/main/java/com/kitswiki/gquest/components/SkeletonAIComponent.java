package com.kitswiki.gquest.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectMap;
import com.kitswiki.gquest.ai.skeleton.SkeletonAI;

public class SkeletonAIComponent implements Component {

    public SkeletonAI skeletonAI;
    public ObjectMap<String, Vector2> locs = new ObjectMap<>();

}
