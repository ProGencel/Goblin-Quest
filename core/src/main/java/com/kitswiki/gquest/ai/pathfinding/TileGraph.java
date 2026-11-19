package com.kitswiki.gquest.ai.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.kitswiki.gquest.ai.LineOfSight;

import static com.kitswiki.gquest.utils.Constants.UNIT_SCALE;

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
    private final Vector2 pa = new Vector2();
    private final Vector2 pb = new Vector2();

    private static final float SHIFT_X = 0f;
    private static final float SHIFT_Y = 0f;

    public float centerX(int x) { return (x + 0.5f - SHIFT_X) * tileSize; }
    public float centerY(int y) { return (y + 0.5f - SHIFT_Y) * tileSize; }

    private int indexX(float wx) { return (int) Math.floor(wx / tileSize + SHIFT_X); }
    private int indexY(float wy) { return (int) Math.floor(wy / tileSize + SHIFT_Y); }

    public TileGraph(World world, TiledMap tiledMap) {
        this.world = world;

        this.width = tiledMap.getProperties().get("width", Integer.class);
        this.height = tiledMap.getProperties().get("height", Integer.class);
        int tilePx = tiledMap.getProperties().get("tilewidth", Integer.class);
        this.tileSize = tilePx * UNIT_SCALE;

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
        final float cx = centerX(x);
        final float cy = centerY(y);

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
        pa.set(centerX(a.x), centerY(a.y));
        pb.set(centerX(b.x), centerY(b.y));
        return LineOfSight.check(world, pa, pb);
    }

    public TileNode at(int x, int y)
    {
        if(x < 0 || y < 0 || x>= width || y>= height)
        {
            return null;
        }
        return nodes[y*width + x];
    }

    public TileNode nearestWalkable(Vector2 p)
    {
        TileNode n = worldToNode(p);
        if(n != null && n.walkable)
        {
            return n;
        }

        int cx = indexX(p.x);
        int cy = indexY(p.y);
        for(int r = 1; r <= 3; r++)
        {
            TileNode best = null;
            float bestD = Float.MAX_VALUE;
            for(int dy = -r; dy <= r; dy++)
            {
                for(int dx = -r; dx<= r; dx++)
                {
                    if(Math.max(Math.abs(dx), Math.abs(dy)) != r)
                    {
                        continue;
                    }
                    TileNode c = at(cx + dx, cy + dy);
                    if(c == null || !c.walkable)
                    {
                        continue;
                    }
                    float d = dx * dx + dy * dy;
                    if(d < bestD)
                    {
                        bestD = d;
                        best = c;
                    }
                }
            }
            if(best != null)
            {
                return best;
            }
        }
        return null;
    }

    public TileNode worldToNode(Vector2 p)
    {
        return at(indexX(p.x), indexY(p.y));
    }

    public Vector2 nodeCenter(TileNode n, Vector2 out)
    {
        return out.set(centerX(n.x), centerY(n.y));
    }

    public boolean findPath(Vector2 from, Vector2 to, DefaultGraphPath<TileNode> out)
    {
        out.clear();
        TileNode s = nearestWalkable(from);
        TileNode e = nearestWalkable(to);
        if(s == null || e == null)
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
