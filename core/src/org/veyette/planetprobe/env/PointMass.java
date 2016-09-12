package org.veyette.planetprobe.env;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Sparky on 9/11/2016.
 */
public class PointMass {
    private Vector2 accel;
    private float damping;

    public Vector2 position;
    public Vector2 velocity;
    public float inverseMass;
    public Vector2 init_position;


    public PointMass(){
        accel = Vector2.Zero;
        damping = .98f;
        position = Vector2.Zero;
        velocity = Vector2.Zero;
        inverseMass = 0;
    }

    public PointMass(Vector2 pos, float invMass){
        accel = Vector2.Zero;
        damping = .98f;
        position = pos;
        init_position = pos.cpy();
        velocity = Vector2.Zero;
        inverseMass = invMass;
    }




    public void applyForce(Vector2 force){
        if(inverseMass > 0) {
            accel.add(force.scl(inverseMass));
        }

    }

    public void increaseDamping(float factor){
        damping *= factor;

    }

    public void update(float delta){
        if(inverseMass == 0){
            position.set(init_position);
        }else {
            velocity.add(accel);

            position.add(velocity.scl(delta));

            accel = Vector2.Zero;


            if (velocity.len2() < 0.00001f * 0.00001f) {
                velocity = Vector2.Zero;
            }


            velocity.scl(damping);
            damping = 0.97f;
        }
    }


}
