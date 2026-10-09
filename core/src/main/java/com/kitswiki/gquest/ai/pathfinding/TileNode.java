package com.kitswiki.gquest.ai.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

public class TileNode {

    public final int x;
    public final int y;
    public final int index;
    public final boolean walkable;
    public final Array<Connection<TileNode>> connections = new Array<>();

    public TileNode(int x, int y,int index, boolean walkable) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.walkable = walkable;
    }
}
