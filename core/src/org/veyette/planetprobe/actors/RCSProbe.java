package org.veyette.planetprobe.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import org.veyette.planetprobe.env.World_env;
import org.veyette.planetprobe.helper.Player;

import java.util.Iterator;

/**
 * Created by Sparky on 9/11/2016.
 */
public class RCSProbe extends Probe{
    public Vector2 accel;
    public boolean thrusting = false;
    public float thrust_amt = 1;
    float mass;
    private Player probe_owner;


    public RCSProbe(Texture texture, Vector2 iposition, Vector2 ivelocity, World_env worldReference, float pmass, Player owner){
        super(texture,  iposition,  ivelocity,  worldReference);
        this.accel = Vector2.Zero;
        probe_owner = owner;
        mass = pmass;
    }

    public void applyThrust(float amt, float delta){
        float ang = getAngle();

        float cosAng = (float) Math.cos(Math.toRadians(ang));
        float sinAng = (float) Math.sin(Math.toRadians(ang));
        this.accel = new Vector2(this.accel.x+ -sinAng * amt * delta, this.accel.y + cosAng * amt * delta);


    }


    public void set_thrust(float amt){
        thrusting = true;
        thrust_amt = amt;

        System.out.println(amt);
        if(amt == 0){
            thrusting = false;
        }

    }



    public void update(float delta) {
         accel = new Vector2(0, 0);

    if(thrusting) {
        System.out.println("Applying thrust");
        applyThrust(thrust_amt, delta);
    }


        for (Star star : worldRef.starList()) {
            accel.add(star.getAccelerationWorld(position));
        }

        for (Planet planet : worldRef.planetList()) {
            accel.add(planet.getAccelerationWorld(position));
        }


        velocity.x += accel.x * mass * delta;
        velocity.y += accel.y * mass * delta;



        velocity.x = velocity.x;
        velocity.y = velocity.y;
        float ang = getAngle();
        float cosAng = (float) Math.cos(Math.toRadians(ang));
        float sinAng = (float) Math.sin(Math.toRadians(ang));

        float vel = velocity.len();
        advance(velocity.x*delta, velocity.y * delta);

        // remove the probe if it leaves the screen
        if (sprite.getY() > worldRef.gameRef.screenHeight || sprite.getY() < 0f || sprite.getX() > worldRef.gameRef.screenWidth || sprite.getX() < 0)
            alive = false;

        for (Star star : worldRef.starList()) {
            if (Intersector.overlaps(star.circle, sprite.getBoundingRectangle())) {
                alive = false;
            }
        }

        for (Planet planet : worldRef.planetList()) {

            if (Intersector.overlaps(planet.circle, sprite.getBoundingRectangle())) {
                alive = false;
                planet.probed = true;
                probe_owner.setCurrent_science_score(probe_owner.getCurrent_science_score() + planet.getScience_value());
            }

        }
    }



}