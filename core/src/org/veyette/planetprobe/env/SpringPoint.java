package org.veyette.planetprobe.env;

/**
 * Created by Sparky on 9/12/2016.
 */

import com.badlogic.gdx.math.Vector2;



import com.badlogic.gdx.math.Vector2;

/**
 * Created by Sparky on 9/11/2016.
 */
public class SpringPoint {
    private Vector2 accel;
    private float damping;

    public Vector2 position;
    public Vector2 anchor;
    public Vector2 velocity;
    public float inverseMass;
    public Vector2 init_position;


    public SpringPoint(){
        accel = Vector2.Zero;
        damping = .98f;
        position = Vector2.Zero;
        anchor = Vector2.Zero;
        velocity = Vector2.Zero;
        inverseMass = 0;
    }

    public SpringPoint(Vector2 pos, Vector2 anch, float invMass){
        accel = Vector2.Zero;
        damping = .98f;
        position = pos;
        anchor = anch;
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

    public void checkPos(){
        Vector2 norm = position.cpy().sub(anchor);
        norm = norm.scl(norm.len2());

        float dist = position.dst(anchor);

        applyForce(norm.scl(-dist*dist));

    }

    public void update(float delta){
        if(inverseMass == 0){
            position.set(anchor);
        }else {

            checkPos();

            velocity.add(accel.scl(delta));
            position.add(velocity);
            accel = Vector2.Zero;

            if (velocity.len2() < 0.0001f * 0.0001f) {
                velocity = Vector2.Zero;
            }


            velocity.scl(damping);
            damping = 0.98f;
        }
    }


}