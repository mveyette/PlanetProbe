package org.veyette.planetprobe.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import org.veyette.planetprobe.env.World_env;

import java.util.Iterator;

/**
 * Created by Sparky on 9/11/2016.
 */
public class Probe{
    public Sprite sprite;
    public Vector2 position;
    public Vector2 velocity;
    private World_env worldRef;
    boolean alive;

    public Probe(Texture texture, Vector2 iposition, Vector2 ivelocity, World_env worldReference){
        sprite = new Sprite(texture);
        worldRef = worldReference;
        alive = true;
        position = iposition;
        velocity = ivelocity;
        sprite.setPosition(position.x, position.y);
        sprite.setRotation(getAngle());
    }

    public float getAngle(){
        if (velocity.y > 0) {
            return -1f * (float) Math.toDegrees(Math.atan(velocity.x / velocity.y));
        } else {
            return (-1f * (float) Math.toDegrees(Math.atan(velocity.x / velocity.y)) + 180f) % 360f;
        }
    }

    public boolean checkAlive(){
        return alive;
    }

    public void render(SpriteBatch sb){
        sprite.draw(sb);
    }


    public void update(float delta) {
        Vector2 accel = new Vector2(0, 0);

        for (Star star : worldRef.starList()) {
            accel.add(star.getAccelerationWorld(position));
        }

        for (Planet planet : worldRef.planetList()) {
            accel.add(planet.getAccelerationWorld(position));
        }


        velocity.x += accel.x * delta;
        velocity.y += accel.y * delta;

        float cosAng = (float) Math.cos(Math.toRadians(getAngle()));
        float sinAng = (float) Math.sin(Math.toRadians(getAngle()));

        float vel = velocity.len();
        advance(-1f * sinAng * vel * delta, cosAng * vel * delta);

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
            }

        }
    }

    public void advance(float dx, float dy){
        position.x += dx;
        position.y += dy;
        sprite.setPosition(position.x, position.y);
        sprite.setRotation(getAngle());
    }

}