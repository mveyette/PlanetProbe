package org.veyette.planetprobe;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import org.veyette.planetprobe.actors.Planet;
import org.veyette.planetprobe.actors.Probe;
import org.veyette.planetprobe.actors.Star;
import org.veyette.planetprobe.env.World_env;

public class GameScreen implements Screen {
    final PlanetProbe game;

    private OrthographicCamera camera;


    private Texture shipImage;
    private Texture probeImage;
    private Texture starImage;
    private Texture planet_jupiterImage;


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


    private void spawnProbe(float launchAngle, float launchSpeed){

        // define position and velocity vectors
        Vector2 position = new Vector2(shipX + shipWidth/2 - probeImage.getWidth()/2, shipY+shipHeight/2 - probeImage.getHeight()/2);
        Vector2 velocity = new Vector2(-1f * (float) Math.sin(Math.toRadians(launchAngle)) * launchSpeed,
                (float) Math.cos(Math.toRadians(launchAngle)) * launchSpeed);

        Probe probe = new Probe(probeImage, position, velocity, gameWorld);
        gameWorld.add_probe(probe);

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

        // create sprites to represent the ship
        ship = new Sprite(shipImage);
        ship.setPosition(shipX, shipY);

        // create our sprite batch and shape render


        // create our star
        Star star = new Star(1f, new Vector2(game.screenWidth/2.0f,game.screenHeight/2.0f), 16, starImage);
        gameWorld.add_Star(star);



        float pmass = (float) Math.pow(10,((Math.random() * (-2 - -6)) + -6));
        float psemiMajorAxis = (float) (Math.random() * (0.25f - 0.01f)) + 0.01f;

        gameWorld.add_planet(new Planet(pmass, star.mass, new Vector2(star.position.x, star.position.y), psemiMajorAxis, 8, 1000000f, planet_jupiterImage));
        planetString += String.format("%.1e", pmass) + " "
                + String.format("%.2f", psemiMajorAxis) + " "
                + "8" + "\n";

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown (int x, int y, int pointer, int button) {
                // your touch down code here
                return true; // return true to indicate the event was handled
            }

            @Override
            public boolean touchUp (int x, int y, int pointer, int button) {
                if (begin) {
                    releasePos.set(x, y, 0);
                    camera.unproject(releasePos);

                    float launchAngle = -1f * (float) Math.toDegrees(Math.atan((releasePos.x - game.screenWidth / 2) / (releasePos.y - shipY - shipHeight / 2)));
                    float launchSpeed = touchPos.sub(new Vector3(shipX + shipWidth / 2, shipY + shipHeight / 2, 0f)).len() * probeLaunchTouchScale;
                    if (launchSpeed > maxLaunchSpeed) {
                        launchSpeed = maxLaunchSpeed;
                    }
                    spawnProbe(launchAngle, launchSpeed);
                    return true; // return true to indicate the event was handled
                }else{
                    begin = true;
                    return false;

                }
            }
        });
    }

        /*
    ####################################################
    The render function, called at ~30-60 fps
    to update game continuously.
    ####################################################
     */

    @Override
    public void render(float delta) {

        // set background color and clear
        Gdx.gl.glClearColor(225f/255f, 225f/255f, 255f/255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // time difference
        deltaTime = Gdx.graphics.getDeltaTime();

        game.batch.setProjectionMatrix(camera.combined);



        shapeRenderer.setProjectionMatrix(camera.combined);
        // batch draw everything

        gameWorld.shapeRenderer.setProjectionMatrix(camera.combined);
        gameWorld.renderGrid(game.batch);

        game.batch.begin();


        //font.draw(batch, probeString, 10, screenHeight-10);

        game.font.draw(game.batch, planetString, game.screenWidth-100, game.screenHeight-10, 100, Align.left, false);
        game.bigfont.draw(game.batch, "DEMO", 5, game.screenHeight-10);

        gameWorld.update(delta);
        gameWorld.render(game.batch);


        ship.draw(game.batch);


        game.batch.end();



        // process user input
        if(Gdx.input.isTouched() && begin) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            shipRotation = -1f * (float) Math.toDegrees(Math.atan((touchPos.x-game.screenWidth/2)/(touchPos.y-shipY-shipHeight/2)));
            ship.setRotation(shipRotation);
            System.out.println(touchPos.x + ", " + touchPos.y);
            gameWorld.bg_grid.applyImplosiveForce(1,new Vector2(touchPos.x, touchPos.y), 100);
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
