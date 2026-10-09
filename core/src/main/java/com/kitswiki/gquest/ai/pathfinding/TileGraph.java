package com.kitswiki.gquest.ai.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.kitswiki.gquest.ai.LineOfSight;

public class TileGraph implements IndexedGraph<TileNode> {

    private static final int[][] DIRS = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1},      // straight
        {1, 1}, {1, -1}, {-1, 1}, {-1, -1}     // diagonal
    };

    public final TileNode[] nodes;
    public final int width;
    public final int height;
    public final float tileSize;

    private final World world;
    private final IndexedAStarPathFinder<TileNode> pathFinder;
    private final Heuristic<TileNode> heuristic = (a,b) -> Vector2.dst(a.x,a.y,b.x,b.y);
    private boolean hit;
    private final Vector2 pa = new Vector2(), pb = new Vector2();

    public TileGraph(World world, int width, int height, float tileSize) {
        this.world = world;
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;

        this.nodes = new TileNode[width * height];

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int i = y * width + x;
                nodes[i] = new TileNode(x, y, i, !isInsideSolid(x, y));
            }
        }

        for (TileNode n : nodes) {
            if (!n.walkable) continue;
            for (int[] d : DIRS) {
                TileNode m = at(n.x + d[0], n.y + d[1]);
                boolean diagonal = d[0] != 0 && d[1] != 0;

                if (diagonal) {
                    // çapraz geçiş: iki yandaki düz geçişlerin hepsi açık olmalı
                    TileNode a = at(n.x + d[0], n.y);
                    TileNode b = at(n.x, n.y + d[1]);
                    if (!canMove(n, a) || !canMove(n, b) || !canMove(a, m) || !canMove(b, m)) continue;
                } else if (!canMove(n, m)) {
                    continue;
                }

                final float cost = diagonal ? 1.414f : 1f;
                n.connections.add(new DefaultConnection<TileNode>(n, m) {
                    @Override public float getCost() { return cost; }
                });
            }
        }
        this.pathFinder = new IndexedAStarPathFinder<>(this);
    }

    private boolean isInsideSolid(int x, int y) {
        hit = false;
        final float cx = (x + 0.5f) * tileSize;
        final float cy = (y + 0.5f) * tileSize;

        world.QueryAABB(fixture -> {
            if (!fixture.isSensor()
                && fixture.getBody().getType() == BodyDef.BodyType.StaticBody
                && fixture.testPoint(cx, cy)) {
                hit = true;
            }
            return !hit;
        }, cx - 0.01f, cy - 0.01f, cx + 0.01f, cy + 0.01f);

        return hit;
    }

    private boolean canMove(TileNode a, TileNode b) {
        if (a == null || b == null || !a.walkable || !b.walkable) return false;
        pa.set((a.x + 0.5f) * tileSize, (a.y + 0.5f) * tileSize);
        pb.set((b.x + 0.5f) * tileSize, (b.y + 0.5f) * tileSize);
        return LineOfSight.check(world, pa, pb);
    }

    private boolean isBlocked(int x, int y) {
        hit = false;
        final float cx = (x + 0.5f) * tileSize;
        final float cy = (y + 0.5f) * tileSize;

        world.QueryAABB(fixture -> {
            if (!fixture.isSensor()
                && fixture.getBody().getType() == BodyDef.BodyType.StaticBody
                && fixture.testPoint(cx, cy)) {
                hit = true;
            }
            return !hit;
        }, cx - 0.01f, cy - 0.01f, cx + 0.01f, cy + 0.01f);

        return hit;
    }

    public TileNode at(int x, int y)
    {
        if(x < 0 || y < 0 || x>= width || y>= height)
        {
            return null;
        }
        return nodes[y*width + x];
    }

    public TileNode worldToNode(Vector2 p)
    {
        return at((int) Math.floor(p.x / tileSize), (int) Math.floor(p.y / tileSize));
    }

    public Vector2 nodeCenter(TileNode n, Vector2 out)
    {
        return out.set((n.x + 0.5f) * tileSize, (n.y + 0.5f) * tileSize);
    }

    public boolean findPath(Vector2 from, Vector2 to, DefaultGraphPath<TileNode> out)
    {
        out.clear();
        TileNode s = worldToNode(from);
        TileNode e = worldToNode(to);
        if(s == null || e == null || !s.walkable || !e.walkable)
        {
            return false;
        }

        return pathFinder.searchNodePath(s,e,heuristic,out);
    }

    @Override
    public int getIndex(TileNode node) {
        return node.index;
    }

    @Override
    public int getNodeCount() {
        return nodes.length;
    }

    @Override
    public Array<Connection<TileNode>> getConnections(TileNode fromNode) {
        return fromNode.connections;
    }
}
