package org.veyette.planetprobe.helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Sparky on 9/11/2016.
 */
public class GlbFuncs {

            /*
        A NOTE ON UNITS
        All position and velocities are in units of pixels and pixels per seconds.
        All physical units are as follows:
            mass         - solar mass
            distance     - 1 AU
            time         - year
            acceleration - AU year^-2
         To convert between world and physical units:
            1 AU  = 960 pixels (2x width of screen)
            1 year = 180 seconds
         */

    public static float sunEarthAcceleration = 39.42f; // AU year^-2
    public static float p2wDist = (2f * 700.0f);
    public static float w2pDist = 1f / p2wDist;
    public static float p2wTime = 180f;
    public static float w2pTime = 1f / p2wTime;



    public static float convert_w2pDist(float wdist){
        return wdist * w2pDist;
    }
    public static float convert_p2wDist(float pdist){
        return pdist * p2wDist;
    }
    public static float convert_w2pTime(float wtime){
        return wtime * w2pTime;
    }
    public static float convert_p2wTime(float ptime){
        return ptime * p2wTime;
    }


    public static Vector2 convert_p2wAccel(Vector2 paccel) {
        return paccel.scl(p2wDist / (float) Math.pow(p2wTime, 2));
    }


    public static float convert_p2wAccelScl(float paccel) {
        return paccel * (p2wDist / (float) Math.pow(p2wTime, 2));
    }

    public static float getVelAngle(Vector2 vec){
        if (vec.y > 0) {
            return -1f * (float) Math.toDegrees(Math.atan(vec.x / vec.y));
        } else {
            return (-1f * (float) Math.toDegrees(Math.atan(vec.x / vec.y)) + 180f) % 360f;
        }
    }


    public static ShapeRenderer debugRenderer = new ShapeRenderer();

    public static void DrawDebugLine(Vector2 start, Vector2 end, int lineWidth, Color color, Matrix4 projectionMatrix)
    {
        Gdx.gl.glLineWidth(lineWidth);
        debugRenderer.setProjectionMatrix(projectionMatrix);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(color);
        debugRenderer.line(start, end);
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    public static void DrawDebugSpot(Vector2 position, Color color, Matrix4 projectionMatrix)
    {
        Gdx.gl.glLineWidth(7);
        debugRenderer.setProjectionMatrix(projectionMatrix);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(color);
        debugRenderer.circle(position.x, position.y, 1);
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    public static void DrawDebugLine(Vector2 start, Vector2 end, Matrix4 projectionMatrix)
    {
        Gdx.gl.glLineWidth(5);
        debugRenderer.setProjectionMatrix(projectionMatrix);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.WHITE);
        debugRenderer.line(start, end);
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }
}
