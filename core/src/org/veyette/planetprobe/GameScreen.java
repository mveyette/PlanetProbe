package org.veyette.planetprobe;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import org.veyette.planetprobe.actors.Planet;
import org.veyette.planetprobe.actors.Probe;
import org.veyette.planetprobe.actors.RCSProbe;
import org.veyette.planetprobe.actors.Star;
import org.veyette.planetprobe.env.World_env;

public class GameScreen implements Screen {
    final PlanetProbe game;

    private OrthographicCamera camera;


    private Texture shipImage;
    private Texture probeImage;
    private Texture starImage;
    private Texture planet_jupiterImage;
    private Texture planet_jupiter_shadowImage;


    private Sprite ship;

    private World_env gameWorld;



    // some helpful display variable
    private float phoneAspectRatio; // not used yet
    private float screenAspectRatio; // not used yet
    private int shipWidth;
    private int shipHeight;
    private int shipY;
    private int shipX;

    // some variables we will use in the app
    private boolean begin = false;

    private Vector3 touchPos = new Vector3();
    private Vector3 releasePos = new Vector3();
    private Vector3 launchVector = new Vector3();

    private float probeLaunchTouchScale;
    private float shipRotation = 0f;
    private float maxLaunchSpeed = 80f;
    private String probeString = "";
    private String planetString = "";
    private float deltaTime;
    public ShapeRenderer shapeRenderer;
    private Sprite grid_bg;
    private ShaderProgram grid_shader;
    private ShaderProgram default_shader;
    Sprite bg;
    RCSProbe probe;
    FPSLogger fpslogger;
    GestureDetector gestureDetector;
    GestureDetector.GestureListener controller;

    class GameGestureController implements GestureDetector.GestureListener {

        float initalScale = 1;
        float initalY = 0;


        public boolean touchDown (float x, float y, int pointer, int button) {
            // your touch down code here
            initalY = y;
            return true; // return true to indicate the event was handled
        }

        public boolean touchDragged(int x, int y, int pointer) {


            return true;
        }


        public boolean touchUp (float x, float y, int pointer, int button) {
            if(probe != null) {
                probe.set_thrust(0);
            }

            return true;
        }


        public boolean tap (float x, float y, int count, int button) {
            Gdx.app.log("GestureDetectorTest", "tap at " + x + ", " + y + ", count: " + count);
            releasePos.set((int)x, (int)y, 0);
            camera.unproject(releasePos);

            float launchAngle = -1f * (float) Math.toDegrees(Math.atan((releasePos.x - game.screenWidth / 2) / (releasePos.y - shipY - shipHeight / 2)));
            float launchSpeed = maxLaunchSpeed;

            spawnProbe(launchAngle, launchSpeed);
            return true; // return true to indicate the event was handled
        }


        public boolean longPress (float x, float y) {
            Gdx.app.log("GestureDetectorTest", "long press at " + x + ", " + y);
            return false;
        }


        public boolean fling (float velocityX, float velocityY, int button) {
            Gdx.app.log("GestureDetectorTest", "fling " + velocityX + ", " + velocityY);

            return false;
        }


        public boolean pan (float x, float y, float deltaX, float deltaY) {
            float dY = (float) 10*Math.abs(initalY - y);
            float max_thrust = 10000;

            if(dY > max_thrust){

                dY = max_thrust;
            }



           // System.out.println("thrust = " + dY);
            if(probe != null) {
                if(initalY - y < 0){
                    probe.set_thrust(-dY);

                }
                else{
                    probe.set_thrust(dY);
                }

            }


            return true;
        }


        public boolean panStop (float x, float y, int pointer, int button) {
            //Gdx.app.log("GestureDetectorTest", "pan stop at " + x + ", " + y);
            if(probe != null) {
                probe.set_thrust(0);
            }
            return true;
        }


        public boolean zoom (float originalDistance, float currentDistance) {
            float ratio = originalDistance / currentDistance;
            camera.zoom = initalScale * ratio;
            System.out.println(camera.zoom);
            return false;
        }

        public boolean pinch (Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer, Vector2 secondPointer) {
            return false;
        }

        public void update () {

        }


        public void pinchStop () {


        }
    }





    public GameScreen(final PlanetProbe gam) {
        this.game = gam;
        gameWorld = new World_env(gam);
        shapeRenderer = new ShapeRenderer();
        // some helpful display variable
        screenAspectRatio = 1f*game.screenHeight / game.screenWidth; // not used yet
        shipWidth = 32;
        shipHeight = 32;
        shipY = 32;
        shipX = game.screenWidth / 2 - shipWidth / 2;


        controller = new GameGestureController();
        gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, controller);
        Gdx.input.setInputProcessor(gestureDetector);
        fpslogger = new FPSLogger();
        // scalar between probe launch line in pixels and launch speed in pixels per second
        probeLaunchTouchScale = 0.5f;

        // grab phone aspect ratio -- not used yet
        phoneAspectRatio = 1f* Gdx.graphics.getHeight() / Gdx.graphics.getWidth();

        // create camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.screenWidth, game.screenHeight);

        // create all our image textures
        shipImage = new Texture(Gdx.files.internal("ship.png"));
        probeImage = new Texture(Gdx.files.internal("probe.png"));
        starImage = new Texture(Gdx.files.internal("star.png"));
        planet_jupiterImage = new Texture(Gdx.files.internal("planet_jupiter.png"));
        planet_jupiter_shadowImage = new Texture(Gdx.files.internal("planet_jupiter_shadow.png"));

        // create sprites to represent the ship
        ship = new Sprite(shipImage);
        ship.setPosition(shipX, shipY);

        // create our planetSprite batch and shape render

        bg = new Sprite(new Texture(Gdx.files.internal("bg.png")));
        bg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        String vertexShader = Gdx.files.internal("gridvertex.glsl").readString();
        String fragmentShader = Gdx.files.internal("gridfragment.glsl").readString();
        grid_shader = new ShaderProgram(vertexShader,fragmentShader);


        Star star = new Star(1f, new Vector2(game.screenWidth/2.0f,game.screenHeight/2.0f), 16, starImage);
        gameWorld.add_Star(star);
       // Star star2 = new Star(1f, new Vector2(game.screenWidth/1.5f,game.screenHeight/1.5f), 16, starImage);
       // gameWorld.add_Star(star2);



        float pmass = .02f;//(float) Math.pow(10,((Math.random() * (-2 - -6)) + -4.1));
        float psemiMajorAxis = .25f;//(float) (Math.random() * (0.25f - 0.01f)) + 0.01f;

        gameWorld.add_planet(new Planet(pmass, star.mass, new Vector2(star.position.x, star.position.y), psemiMajorAxis, 8, 1000000f, planet_jupiterImage, planet_jupiter_shadowImage));

        gameWorld.add_planet(new Planet(pmass*9.5f, star.mass, new Vector2(star.position.x, star.position.y), psemiMajorAxis*.75f, 8, 700000f, planet_jupiterImage, planet_jupiter_shadowImage));
        planetString += String.format("%.1e", pmass) + " "
                + String.format("%.2f", psemiMajorAxis) + " "
                + "8" + "\n";

    }



    private void spawnProbe(float launchAngle, float launchSpeed){

        // define position and velocity vectors
        Vector2 position = new Vector2(shipX + shipWidth/2 - probeImage.getWidth()/2, shipY+shipHeight/2 - probeImage.getHeight()/2);
        Vector2 velocity = new Vector2(-1f * (float) Math.sin(Math.toRadians(launchAngle)) * launchSpeed,
                (float) Math.cos(Math.toRadians(launchAngle)) * launchSpeed);
        probe = new RCSProbe(probeImage, position, velocity, gameWorld);
        gameWorld.add_probe(probe);
    }

        /*
    ####################################################
    The render function, called at ~30-60 fps
    to update game continuously.
    ####################################################
     */


    public void handleInput() {
    }

    public void update(float delta){
        handleInput();
    }

    @Override
    public void render(float delta) {
       // fpslogger.log();
        // set background color and clear
       // Gdx.gl.glClearColor(0.5f, 0.7f, 1f, 1f);
        Gdx.gl.glClearColor(0.01f, 0.01f, .01f, 1f);
        //controller.update();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // time difference
        deltaTime = Gdx.graphics.getDeltaTime();

        game.batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        gameWorld.shapeRenderer.setProjectionMatrix(camera.combined);
        gameWorld.update(delta);
        update(deltaTime);

        game.batch.begin();
        gameWorld.renderGrid(game.batch);
       // game.font.draw(game.batch, planetString, game.screenWidth-100, game.screenHeight-10, 100, Align.left, false);
        //game.bigfont.draw(game.batch, "DEMO", 5, game.screenHeight-10);

        gameWorld.render(game.batch);
        ship.draw(game.batch);
        game.batch.end();



        // process user input
        if(Gdx.input.isTouched() && begin) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            shipRotation = -1f * (float) Math.toDegrees(Math.atan((touchPos.x-game.screenWidth/2)/(touchPos.y-shipY-shipHeight/2)));
            ship.setRotation(shipRotation);



            // render line between ship and touchPos
            launchVector.set(shipX+shipWidth/2, shipY+shipHeight/2, 0f);
            launchVector.sub(touchPos).scl(-1f);
            float launchSpeed = launchVector.len() * probeLaunchTouchScale;
            if (launchSpeed > maxLaunchSpeed){
                launchVector.scl(maxLaunchSpeed / launchSpeed);
            }
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.line(shipX+shipWidth/2, shipY+shipHeight/2, (shipX+shipWidth/2)+launchVector.x, (shipY+shipHeight/2)+launchVector.y);
            shapeRenderer.end();
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        shipImage.dispose();
        probeImage.dispose();
    }
}
