package com.kitswiki.gquest.ai;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

public class LineOfSight implements RayCastCallback {

    private static final LineOfSight instance = new LineOfSight();
    private boolean blocked;

    public static boolean check(World world, Vector2 from, Vector2 to)
    {
        if(from.epsilonEquals(to, 0.001f))
        {
            return true;
        }

        instance.blocked = false;
        world.rayCast(instance, from, to);
        return !instance.blocked;
    }

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        if(fixture.isSensor())
        {
            return -1;
        }
        if(fixture.getBody().getType() != BodyDef.BodyType.StaticBody)
        {
            return -1;
        }
        blocked = true;
        return 0;
    }
}
