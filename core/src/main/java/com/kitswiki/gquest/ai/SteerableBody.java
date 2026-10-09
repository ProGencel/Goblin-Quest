package com.kitswiki.gquest.ai;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class SteerableBody implements Steerable<Vector2> {

    private final Body body;
    private final float boundingRadius;
    private boolean tagged;

    private float maxLinearSpeed = 2f;
    private float maxLinearAcceleration = 10f;
    private float maxAngularSpeed = 0f;
    private float maxAngularAcceleration = 0f;
    private float zeroLinearSpeedThreshold = 0.01f;

    private SteeringBehavior<Vector2> behavior;
    private final SteeringAcceleration<Vector2> steeringOutput =
        new SteeringAcceleration<>(new Vector2());

    public SteerableBody(Body body, float boundingRadius) {
        this.body = body;
        this.boundingRadius = boundingRadius;
    }

    public void setBehavior(SteeringBehavior<Vector2> behavior) {
        this.behavior = behavior;
    }

    public void update(float delta) {
        if (behavior == null) return;

        behavior.calculateSteering(steeringOutput);

        Vector2 vel = body.getLinearVelocity().cpy()
            .mulAdd(steeringOutput.linear, delta);

        if (vel.len() > maxLinearSpeed) vel.setLength(maxLinearSpeed);

        body.setLinearVelocity(vel);
    }

    public void stop()
    {
        behavior = null;
        body.setLinearVelocity(0,0);
    }

    @Override public Vector2 getPosition() { return body.getPosition(); }
    @Override public float getOrientation() { return body.getAngle(); }
    @Override public void setOrientation(float o) { body.setTransform(body.getPosition(), o); }
    @Override public Vector2 getLinearVelocity() { return body.getLinearVelocity(); }
    @Override public float getAngularVelocity() { return body.getAngularVelocity(); }
    @Override public float getBoundingRadius() { return boundingRadius; }
    @Override public boolean isTagged() { return tagged; }
    @Override public void setTagged(boolean t) { tagged = t; }

    @Override public float getZeroLinearSpeedThreshold() { return zeroLinearSpeedThreshold; }
    @Override public void setZeroLinearSpeedThreshold(float v) { zeroLinearSpeedThreshold = v; }
    @Override public float getMaxLinearSpeed() { return maxLinearSpeed; }
    @Override public void setMaxLinearSpeed(float v) { maxLinearSpeed = v; }
    @Override public float getMaxLinearAcceleration() { return maxLinearAcceleration; }
    @Override public void setMaxLinearAcceleration(float v) { maxLinearAcceleration = v; }
    @Override public float getMaxAngularSpeed() { return maxAngularSpeed; }
    @Override public void setMaxAngularSpeed(float v) { maxAngularSpeed = v; }
    @Override public float getMaxAngularAcceleration() { return maxAngularAcceleration; }
    @Override public void setMaxAngularAcceleration(float v) { maxAngularAcceleration = v; }

    @Override
    public float vectorToAngle(Vector2 v) { return (float) Math.atan2(-v.x, v.y); }
    @Override
    public Vector2 angleToVector(Vector2 out, float angle) {
        out.x = -(float) Math.sin(angle);
        out.y = (float) Math.cos(angle);
        return out;
    }
    @Override
    public Location<Vector2> newLocation() { return new SteerableBody(body, boundingRadius); }
}
