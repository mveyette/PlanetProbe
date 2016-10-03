package org.veyette.planetprobe.helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Sparky on 10/2/2016.
 */
public class guiSlideBar {

    public float posx;
    public float posy;
    public Sprite barSprite;
    public Sprite ballSprite;
    public float deltaY;
    public float maxVal;
    public float minVal;
    public boolean show;

    public guiSlideBar(float px, float py, float max, float min){
        barSprite = new Sprite(new Texture(Gdx.files.internal("guislide.png")));
        ballSprite = new Sprite(new Texture(Gdx.files.internal("guibutton.png")));
        maxVal = max;
        minVal = min;
        deltaY = 0;
        posx = px;
        posy = py;
        show = false;
    }

    public void setDeltaY(float del){
        this.deltaY = del;
    }

    public void show(boolean tf){
        show = tf;
    }
    public void setPos(float x, float y){
        posx = x;
        posy = y;
    }


    public Color calculateColor(){
      float factor = Math.abs(deltaY)/maxVal;
        return new Color(factor, .1f, 1f-factor, .45f);

    }
    public void render(SpriteBatch sb){
        ballSprite.setColor(calculateColor());
    if(show) {
        ballSprite.setPosition(posx - ballSprite.getWidth() / 2, (posy- ballSprite.getWidth() / 2));
        ballSprite.draw(sb);
    }


    }
}
