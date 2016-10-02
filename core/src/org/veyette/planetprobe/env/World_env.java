package org.veyette.planetprobe.env;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

import org.veyette.planetprobe.PlanetProbe;
import org.veyette.planetprobe.actors.Planet;
import org.veyette.planetprobe.actors.Probe;
import org.veyette.planetprobe.actors.Star;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Sparky on 9/11/2016.
 */
public class World_env {

    public PlanetProbe gameRef;
    private ArrayList<Probe> probes;
    private ArrayList<Star> stars;
    private ArrayList<Planet> planets;
    public ShapeRenderer shapeRenderer;
    public Grid bg_grid;

    public World_env(PlanetProbe gameReference){
        gameRef = gameReference;
        probes = new ArrayList<Probe>();
        planets = new ArrayList<Planet>();
        stars = new ArrayList<Star>();
        shapeRenderer = new ShapeRenderer();
        bg_grid = new Grid(gameRef.screenWidth, gameRef.screenHeight, gameRef.screenWidth/23, gameRef.screenHeight/32, 8f, this);

    }

    public void add_probe(Probe pb){
        probes.add(pb);
    }

    public void add_planet(Planet plt){
        planets.add(plt);
    }

    public void add_Star(Star str){
        stars.add(str);
    }

    public ArrayList<Star> starList(){
        return stars;
    }

    public ArrayList<Planet> planetList(){
        return planets;
    }

    public ArrayList<Probe> probeList(){
        return probes;
    }

    public void renderGrid(SpriteBatch sb){
        bg_grid.render(sb);
    }

    public void render(SpriteBatch sb){


        for (Star star: stars){
            star.sprite.draw(sb);
        }

        for (Planet planet: planets){
                planet.render(sb);
        }

        for(Probe probe: probes) {
            probe.render(sb);
        }
    }

    public void cull(){

        Iterator<Probe> probeIterator = probes.iterator();

        while (probeIterator.hasNext()) {

            Probe probe = probeIterator.next();

            if(probe.checkAlive() == false){
                probeIterator.remove();
            }
        }
    }

    public void update(float delta){
        bg_grid.update(delta);

        for (Star star: stars){
            star.update(delta);
        }

        for (Planet planet: planets){
            planet.update(delta);
        }

        for(Probe probe: probes) {
            probe.update(delta);
        }

        cull();
    }

}
