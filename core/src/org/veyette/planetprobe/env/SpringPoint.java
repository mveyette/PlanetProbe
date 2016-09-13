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
    public Vector2 accel;
    private float damping;
    public float stiffness;
    public Vector2 position;
    public Vector2 anchor;
    public Vector2 velocity;
    public float inverseMass;
    public Vector2 init_position;
    public int id;


    public SpringPoint(){
        accel = new Vector2(0,0);
        damping = .90f;
        position = new Vector2(0,0);
        anchor = new Vector2(0,0);
        velocity = new Vector2(0,0);
        inverseMass = 0;
    }

    public SpringPoint(Vector2 pos, Vector2 anch, float stiff_val, float invMass, int ID){
        accel = new Vector2(0,0);
        damping = .9f;
        id = ID;
        stiffness = stiff_val;
        position = pos;
        anchor = anch;
        init_position  = new Vector2(pos.x,pos.y);
        velocity = new Vector2(0,0);
        inverseMass = invMass;
    }




    public void applyForce(Vector2 force){
        accel = new Vector2(accel.x + force.x*inverseMass, accel.y + force.y*inverseMass);
    }



    public float getLength(){
        return position.cpy().sub(anchor).len();
    }

    public void checkPos(){
        Vector2 norm = position.cpy().sub(anchor);
        float lensq = norm.len2();
        if(lensq != 0) {
            norm = new Vector2(norm.x / lensq, norm.y / lensq);


            float dist = position.dst2(anchor)*stiffness;

            applyForce(new Vector2(norm.x*-dist*dist, norm.y*-dist*dist));
        }
    }

    public void update(float delta){


        if(inverseMass == 0){
            position.set(anchor);
        }

        else {

            checkPos();

            velocity.x += accel.x * delta;
            velocity.y += accel.y * delta;

            //System.out.println(accel.toString());
            if(velocity.len() > 2.2){
               velocity = velocity.nor().scl(2.2f);

            }

            position.x += velocity.x;
            position.y += velocity.y;

            this.position = new Vector2(position.x, position.y);


            this.accel = Vector2.Zero;

            if (velocity.len2() < 0.1f * .1f) {
                velocity = Vector2.Zero;
            }


            this.velocity = new Vector2(velocity.x*damping, velocity.y*damping);

        }
    }


}