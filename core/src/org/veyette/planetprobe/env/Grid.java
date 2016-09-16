package org.veyette.planetprobe.env;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import org.veyette.planetprobe.actors.Mass;
import org.veyette.planetprobe.actors.Planet;
import org.veyette.planetprobe.actors.Star;
import org.veyette.planetprobe.helper.GlbFuncs;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Sparky on 9/11/2016.
 */
public class Grid {

    Hashtable<Integer,SpringPoint> points;
    int cols;
    int rows;
    float stiff_val;
    World_env gameWorld;
    Sprite point_sprite;

    public Grid(int width, int height, int spacingX, int spacingY, float stiffness, World_env worldRef){
        int numColumns = (int)(width/spacingX)+1;
        int numRows = (int)(height/spacingY)+1;

        gameWorld = worldRef;
        stiff_val = stiffness;

        points = new Hashtable<Integer,SpringPoint>();

        cols = numColumns;
        rows = numRows;
        point_sprite = new Sprite(createBgTexture());
        int column = 0, row = 0;
        int count = 0;

        for (int y = 0; y <= height; y += spacingY)
        {
            for (int x = 0; x <= width; x += spacingX)
            {
                float invmass = 1f;
                points.put(count,new SpringPoint(new Vector2(x, y), new Vector2(x, y), stiffness, invmass, count) );
                column++;
                count++;
            }
            row++;
            column = 0;
        }

    }

    public static Texture createBgTexture()
    {
        Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA4444); // or RGBA8888
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap); // must be manually disposed
        pixmap.dispose();

        return texture;
    }

    public Vector2 calculate_accel_body(Mass p, Vector2 position){
        Vector2 norm = new Vector2(p.position.x-position.x, p.position.y - position.y);
        float len = norm.len();
        float Gfactor = 30000.0f;

        if(len != 0 ) {
            norm = norm.nor();
            float dist = position.dst2(p.position);
            return new Vector2(norm.x * (p.mass*Gfactor)/(dist), norm.y * (p.mass*Gfactor)/(dist));
        }
        return Vector2.Zero;
    }

    public void update(float delta){

        for(int i = 0; i < points.size(); i++) {
            SpringPoint sp = points.get(i);

            for (Star star : gameWorld.starList()) {
                Vector2 paccel = calculate_accel_body(star, sp.position);
                sp.applyForce(paccel);
            }

            for (Planet pl : gameWorld.planetList()) {
                Vector2 paccel = calculate_accel_body(pl, sp.position);
                sp.applyForce(paccel);
            }

            sp.update(delta);
        }

    }

    public Color calculatePointColor(float extendLength, float bp1, float bp2, float bp3, float a){
        float r = 0.0f;
        float g = 0.0f;
        float b = 0.0f;



        if(extendLength < bp1){
            b = .5f + extendLength/bp1;
        }

        else if(extendLength < bp2){
            g = .5f + (extendLength - bp1)/bp2;
            b = 1.0f - (extendLength - bp1)/(bp2 - bp1);
        }


        else if(extendLength >= bp2){
            g = 1.0f - (extendLength - (bp2))/(bp3 - bp2);

            if(g <0){
                g = 0;
            }
            r = .5f + ((extendLength - (bp2)))/(bp3 - bp2);
           // System.out.println(r);
        }

        return new Color(r,g,b,a);
    }

    public void render(SpriteBatch sb)
    {

        int width = cols;
        int height = rows;
        int linewidth = 1;

       // Matrix4 projection = gameWorld.shapeRenderer.getProjectionMatrix();

      //  Gdx.gl.glLineWidth(7);

       // GlbFuncs.debugRenderer.setProjectionMatrix(projection);
       // GlbFuncs.debugRenderer.begin(ShapeRenderer.ShapeType.Filled);


        for(int i = 0; i < points.size(); i++) {
            SpringPoint sp = points.get(i);
            float len = sp.getLength();
            Color col =  calculatePointColor(len, .001f, .3f, 1f, 1f);
            point_sprite.setColor(col);
            point_sprite.setPosition(sp.position.x, sp.position.y);
            point_sprite.draw(sb);
               // GlbFuncs.debugRenderer.setColor(col);
                //GlbFuncs.debugRenderer.line(sp.position.x, sp.position.y,0, sp.position.x+1, sp.position.y+1, 0);
        }

       // GlbFuncs.debugRenderer.end();
       // Gdx.gl.glLineWidth(1);



    }


}
