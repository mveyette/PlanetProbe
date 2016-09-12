package org.veyette.planetprobe.env;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import org.veyette.planetprobe.helper.GlbFuncs;

import java.util.ArrayList;

/**
 * Created by Sparky on 9/11/2016.
 */
public class Grid {

    ArrayList<SpringPoint> points;
    int cols;
    int rows;
    World_env gameWorld;

    public Grid(float width, float height, float spacingX, float spacingY, World_env worldRef){
        int numColumns = (int)(width/spacingX)+1;
        int numRows = (int)(height/spacingY)+1;
        gameWorld = worldRef;
        points = new ArrayList<SpringPoint>();

        cols = numColumns;
        rows = numRows;

        int column = 0, row = 0;

        for (float y = 0; y <= height; y += spacingY)
        {
            for (float x = 0; x <= width; x += spacingX)
            {
                System.out.println(width);
                System.out.println(x);
                float invmass = 1.0f;

                if(x <= 0 || y <=0 || x >= width || y >= height-spacingY) {
                    invmass = 0;
                    System.out.println("inv0");
                }
                    AddPointMass(points, column, row, new SpringPoint(new Vector2(x, y), new Vector2(x, y), invmass));

                column++;
            }
            row++;
            column = 0;
        }

    }


    public void AddPointMass(ArrayList<SpringPoint> array, int x, int y, SpringPoint inp)
    {
        array.add(inp);
    }

    public SpringPoint GetPointMass(ArrayList<SpringPoint> array, int x, int y)
    {
        return array.get(y * cols + x);
    }


    public void applyImplosiveForce(float force, Vector2 position, float radius)
    {
        for (int i = 0; i < cols * rows; i++)
        {
            float dist2 = position.dst2(points.get(i).position);
            if (dist2 < radius * radius)
            {
                points.get(i).applyForce((position.cpy().sub(points.get(i).position)).scl(100.0f * force).scl(1/(100 + dist2)));
                points.get(i).increaseDamping(0.9f);
            }
        }
    }

    public void update(float delta){


        for(int i = 0; i < points.size(); i++)
        {
            points.get(i).update(delta);

        }

    }


    public float catmullRom( float value1,  float value2,  float value3,  float value4, float amount)
    {

        float amountSquared = amount * amount;
        float amountCubed = amountSquared * amount;
        return (float)(0.5f * (2.0f * value2 +
                (value3 - value1) * amount +
                (2.0f * value1 - 5.0f * value2 + 4.0f * value3 - value4) * amountSquared +
                (3.0f * value2 - value1 - 3.0f * value3 + value4) * amountCubed));
    }

    public Vector2 catmullRom( Vector2 value1, Vector2 value2, Vector2 value3, Vector2 value4, float amount)
    {
        return new Vector2(catmullRom(value1.x, value2.x, value3.x, value4.x, amount), catmullRom(value1.y, value2.y, value3.y, value4.y, amount));
    }


    public void render(SpriteBatch sb)
    {

        int width = cols;
        int height = rows;
        int linewidth = 4;
        Color col =  Color.BLACK;
        Matrix4 projection = gameWorld.shapeRenderer.getProjectionMatrix();


        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++) {

                if (x == 0 || y == 0 ) {
                    Vector2 p = GetPointMass(points, x, y).position;
                    if (GetPointMass(points, x, y).inverseMass == 0) {
                        GlbFuncs.DrawDebugSpot(p, Color.RED, projection);
                    }
                } else {
                    Vector2 left, up;
                    Vector2 p = GetPointMass(points, x, y).position;
                    GlbFuncs.DrawDebugSpot(p, col, projection);

                    if (GetPointMass(points, x, y).inverseMass == 0) {
                        GlbFuncs.DrawDebugSpot(p, Color.RED, projection);
                    }

                }
            }
        }



    }


}
