package org.veyette.planetprobe.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

import org.veyette.planetprobe.helper.GlbFuncs;

/**
 * Created by Sparky on 9/11/2016.
 */
public class Planet extends Mass{
    public Circle circle;
    public Sprite planetSprite;
    public Sprite shadowSprite;
    public float semiMajorAxis; // in physical units
    public float orbitalPeriod; // in physical units
    public float rotationalPeriod; // in physical units
    public float rotationalAngle;
    public boolean probed;
    public Vector2 star_pos;
    private int science_value;
    private float distanceMoved;
    private float angle;


    public Planet(float imass, float starmass, Vector2 star_position, float isemiMajorAxis, int ipxradius, float irotationalPeriod, Texture planetImage, Texture shadowImage, int sci_val){
        super(imass);
        star_pos = star_position;
        probed = false;
        semiMajorAxis = isemiMajorAxis;
        pxradius = ipxradius;
        rotationalPeriod = irotationalPeriod;
        //position.x = screenWidth/2;
        //position.y = star.position.y - convert_p2wDist(semiMajorAxis);
        angle = 0f;
        orbitalPeriod = (float) Math.sqrt(Math.pow(semiMajorAxis,3d) / starmass);
        //circle = new Circle(position.x-pxradius/2, position.y-pxradius/2, pxradius);
        circle = new Circle(0f, 0f, pxradius);
        planetSprite = new Sprite(planetImage);
        shadowSprite = new Sprite(shadowImage);
        rotationalAngle = 0f;
        update(0f);
        science_value = sci_val;
    }

    public void render(SpriteBatch sb){
            planetSprite.draw(sb);
            shadowSprite.draw(sb);
    }

    public int getScience_value(){

        return science_value;
    }


    public void update(float delta){
        // advance the planets position
        //distanceMoved = convert_p2wDist(2f * (float) Math.PI * semiMajorAxis * (convert_w2pTime(dt) / period));
        //angle = star.getAngleTo(position);
        //position.x += (float) Math.cos(Math.toRadians(angle)) * distanceMoved;
        //position.y += (float) Math.sin(Math.toRadians(angle)) * distanceMoved;
        angle += 360f * (GlbFuncs.convert_w2pTime(delta) /orbitalPeriod);
        position.x = star_pos.x + GlbFuncs.convert_p2wDist(semiMajorAxis) * (float) Math.cos(Math.toRadians(angle));
        position.y = star_pos.y + GlbFuncs.convert_p2wDist(semiMajorAxis) * (float) Math.sin(Math.toRadians(angle));
        circle.setX(position.x);
        circle.setY(position.y);
        planetSprite.setPosition(position.x- planetSprite.getWidth()/2, position.y- planetSprite.getHeight()/2);
        rotationalAngle += 360. * (delta/GlbFuncs.convert_p2wTime(rotationalPeriod));
        planetSprite.setRotation(rotationalAngle);
        shadowSprite.setPosition(position.x- shadowSprite.getWidth()/2, position.y- shadowSprite.getHeight()/2);
        shadowSprite.setRotation(GlbFuncs.getVelAngle(new Vector2(position).sub(star_pos)) - 90f);
    }
}