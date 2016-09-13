package org.veyette.planetprobe.actors;

import com.badlogic.gdx.math.Vector2;

import org.veyette.planetprobe.helper.GlbFuncs;

/**
 * Created by Sparky on 9/11/2016.
 */
public class Mass{
    public Vector2 position;
    public float mass; // in solar masses

    private float totalAcceleration;
    private float angle;
    public int pxradius = 0;

    public Mass(float imass){
        // need to define position in extended classes
        position = new Vector2(0f,0f);
        mass = imass;
    }

    public float getTotalAccelerationPhysical(Vector2 pos){
        return GlbFuncs.sunEarthAcceleration * mass / (float) Math.pow(GlbFuncs.convert_w2pDist(position.dst(pos)), 2);
    }

    public float getTotalAccelerationWorld(Vector2 pos){
        return GlbFuncs.convert_p2wAccelScl(getTotalAccelerationPhysical(pos));
    }

    public float getAngleTo(Vector2 pos) {
        return (GlbFuncs.getVelAngle(new Vector2(position).sub(pos)));
    }

    // returns acceleration in physical units
    public Vector2 getAccelerationPhysical(Vector2 pos){
        totalAcceleration = getTotalAccelerationPhysical(pos);
        angle = getAngleTo(pos);
        return new Vector2(-1f * (float) Math.sin(Math.toRadians(angle)) * totalAcceleration,
                (float) Math.cos(Math.toRadians(angle)) * totalAcceleration);
            /*
            if(pos.y <= position.y){
                return new Vector3(-1f * (float) Math.sin(Math.toRadians(angle)) * totalAcceleration,
                                         (float) Math.cos(Math.toRadians(angle)) * totalAcceleration, 0f);
            } else {
                return new Vector3(      (float) Math.sin(Math.toRadians(angle)) * totalAcceleration,
                                   -1f * (float) Math.cos(Math.toRadians(angle)) * totalAcceleration, 0f);
            }
            */
    }

    public Vector2 getAccelerationWorld(Vector2 pos) {
        return GlbFuncs.convert_p2wAccel(getAccelerationPhysical(pos));
    }
}