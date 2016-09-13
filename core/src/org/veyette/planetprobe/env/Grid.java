package org.veyette.planetprobe.env;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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

/**
 * Created by Sparky on 9/11/2016.
 */
public class Grid {

    SpringPoint[][] points;
    SpringPoint[] linear_points;
    int cols;
    int rows;
    float stiff_val;
    World_env gameWorld;

    public Grid(int width, int height, int spacingX, int spacingY, float stiffness, World_env worldRef){
        int numColumns = (int)(width/spacingX)+1;
        int numRows = (int)(height/spacingY)+1;
        gameWorld = worldRef;
        stiff_val = stiffness;
        points = new SpringPoint[numRows][numColumns];

        cols = numColumns;
        rows = numRows;

        int column = 0, row = 0;
        int count = 0;

        for (int y = 0; y <= height; y += spacingY)
        {
            for (int x = 0; x <= width; x += spacingX)
            {
                float invmass = 1f;
                points[row][column] = new SpringPoint(new Vector2(x, y), new Vector2(x, y), stiffness, invmass, count);

                column++;
                count++;
            }
            row++;
            column = 0;
        }

        linear_points = unravel(points);

    }


    public SpringPoint[] unravel(SpringPoint[][] array) {
        int r = array.length;
        if (r == 0) {
            return new SpringPoint[0]; // Special case: zero-length array
        }
        int c = array[0].length;
        SpringPoint[] result = new SpringPoint[r * c];
        int index = 0;
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                result[index] = array[i][j];
                index++;
            }
        }
        return result;
    }


    public Vector2 calculate_accel_body(Mass p, Vector2 position){
        Vector2 norm = new Vector2(p.position.x-position.x, p.position.y - position.y);
        float len = norm.len();
        float Gfactor = 30000.0f;


        if(len != 0 ) {
            norm = norm.nor();

            float dist = position.dst(p.position);

            //System.out.println(new Vector2(norm.x * (p.mass)/(dist), norm.y * (p.mass)/(dist)).toString());
            return new Vector2(norm.x * (p.mass*Gfactor)/(dist * dist), norm.y * (p.mass*Gfactor)/(dist * dist));
        }


        return Vector2.Zero;
    }



    public void update(float delta){

        for(int i = 0; i < linear_points.length; i++) {

                for (Star star : gameWorld.starList()) {
                    Vector2 paccel = calculate_accel_body(star, linear_points[i].position);
                    linear_points[i].applyForce(paccel);
                }

            for (Planet pl : gameWorld.planetList()) {
                Vector2 paccel = calculate_accel_body(pl, linear_points[i].position);
                linear_points[i].applyForce(paccel);
            }

                linear_points[i].update(delta);
            }


        }




    public void render(SpriteBatch sb)
    {

        int width = cols;
        int height = rows;
        int linewidth = 1;

        Matrix4 projection = gameWorld.shapeRenderer.getProjectionMatrix();

        Gdx.gl.glLineWidth(7);

        GlbFuncs.debugRenderer.setProjectionMatrix(projection);
        GlbFuncs.debugRenderer.begin(ShapeRenderer.ShapeType.Filled);



        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++) {
                Vector2 p = points[y][x].position;
                Vector2 pi = points[y][x].init_position;
                Color col =  new Color((float)points[y][x].getLength()/10.0f,.15f,.15f,.5f);
                //GlbFuncs.DrawDebugSpot(pi, Color.BLACK, projection);
                if(!points[y][x].is_asleep) {
                    GlbFuncs.debugRenderer.setColor(col);
                    GlbFuncs.debugRenderer.circle(p.x, p.y, 1);
                 //   GlbFuncs.DrawDebugSpot(p, col, projection);
                }

            }
        }
        GlbFuncs.debugRenderer.end();
        Gdx.gl.glLineWidth(1);



    }


}
