package org.veyette.planetprobe.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Sparky on 9/11/2016.
 */
public class Star extends Mass {
    public Sprite sprite;
    public Circle circle;
     // in pixels

    public Star(float imass, Vector2 _position, int ipxradius, Texture image){
        super(imass);
        position = _position;
        sprite = new Sprite(image);
        sprite.setPosition(position.x-sprite.getWidth()/2, position.y-sprite.getHeight()/2);
        pxradius = ipxradius;
        circle = new Circle(position.x-pxradius/2, position.y-pxradius/2, pxradius);
    }


    public void update(float delta){}
}