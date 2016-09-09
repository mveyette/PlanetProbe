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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class GameScreen implements Screen {
    final PlanetProbe game;

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Texture shipImage;
    private Texture probeImage;
    private Sprite ship;
    private Array<Probe> probes;
    private Star star;
    private Array<Planet> planets;

    // world-to-physical (w2p) and physical-to-world (p2w) conversions
    private float sunEarthAcceleration;
    private float p2wDist;
    private float w2pDist;
    private float p2wTime;
    private float w2pTime;

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

    // world and physical unit conversion functions
    private float convert_w2pDist(float wdist){
        return wdist * w2pDist;
    }
    private float convert_p2wDist(float pdist){
        return pdist * p2wDist;
    }
    private float convert_w2pTime(float wtime){
        return wtime * w2pTime;
    }
    private float convert_p2wTime(float ptime){
        return ptime * p2wTime;
    }
    private Vector3 convert_p2wAccel(Vector3 paccel) {
        return paccel.scl(p2wDist / (float) Math.pow(p2wTime, 2));
    }
    private float convert_p2wAccelScl(float paccel) {
        return paccel * (p2wDist / (float) Math.pow(p2wTime, 2));
    }

    private float getVelAngle(Vector3 vec){
        if (vec.y > 0) {
            return -1f * (float) Math.toDegrees(Math.atan(vec.x / vec.y));
        } else {
            return (-1f * (float) Math.toDegrees(Math.atan(vec.x / vec.y)) + 180f) % 360f;
        }
    }

    // probe class
    private class Probe{
        public Sprite sprite;
        public Vector3 position;
        public Vector3 velocity;

        public Probe(Texture texture, Vector3 iposition, Vector3 ivelocity){
            sprite = new Sprite(texture);
            position = iposition;
            velocity = ivelocity;
            sprite.setPosition(position.x, position.y);
            sprite.setRotation(getAngle());
        }

        public float getAngle(){
            return getVelAngle(velocity);
            /*
            if (velocity.y > 0) {
                return getVelAngle(velocity);
            } else{
                return (getVelAngle(velocity) + 180) % 360;
            }
            */
        }

        public void advance(float dx, float dy){
            position.x += dx;
            position.y += dy;
            sprite.setPosition(position.x, position.y);
            sprite.setRotation(getAngle());
        }

    }

    // a class for a massive object
    private class Mass{
        public Vector3 position;
        public float mass; // in solar masses

        private float totalAcceleration;
        private float angle;

        public Mass(float imass){
            // need to define position in extended classes
            position = new Vector3(0f,0f,0f);
            mass = imass;
        }

        public float getTotalAccelerationPhysical(Vector3 pos){
            return sunEarthAcceleration * mass / (float) Math.pow(convert_w2pDist(position.dst(pos)), 2);
        }

        public float getTotalAccelerationWorld(Vector3 pos){
            return convert_p2wAccelScl(getTotalAccelerationPhysical(pos));
        }

        public float getAngleTo(Vector3 pos) {
            return (getVelAngle(new Vector3(position).sub(pos)));
        }

        // returns acceleration in physical units
        public Vector3 getAccelerationPhysical(Vector3 pos){
            totalAcceleration = getTotalAccelerationPhysical(pos);
            angle = getAngleTo(pos);
            return new Vector3(-1f * (float) Math.sin(Math.toRadians(angle)) * totalAcceleration,
                    (float) Math.cos(Math.toRadians(angle)) * totalAcceleration, 0f);
            /*
            if(pos.y <= position.y){
                return new Vector3(-1f * (float) Math.sin(Math.toRadians(angle)) * totalAcceleration,
                                         (float) Math.cos(Math.toRadians(angle)) * totalAcceleration, 0f);
            } else {
                return new Vector3(      (float) Math.sin(Math.toRadians(angle)) * totalAcceleration,
                                   -1f * (float) Math.cos(Math.toRadians(angle)) * totalAcceleration, 0f);
            }
            */
        }

        public Vector3 getAccelerationWorld(Vector3 pos) {
            return convert_p2wAccel(getAccelerationPhysical(pos));
        }
    }

    // star class
    private class Star extends Mass{
        public Circle circle;
        public int pxradius; // in pixels

        private Star(float imass, int ipxradius){
            super(imass);
            position.x = game.screenWidth/2;
            position.y = Math.round(game.screenHeight/2)+pxradius/2;
            pxradius = ipxradius;
            circle = new Circle(position.x-pxradius/2, position.y-pxradius/2, pxradius);
        }
    }

    private class Planet extends Mass{
        public Circle circle;
        public int pxradius; // in pixels
        public float semiMajorAxis; // in physical units
        public float period; // in physical units\
        public boolean probed;

        private float distanceMoved;
        private float angle;

        private Planet(float imass, float isemiMajorAxis, int ipxradius){
            super(imass);
            probed = false;
            semiMajorAxis = isemiMajorAxis;
            pxradius = ipxradius;
            //position.x = screenWidth/2;
            //position.y = star.position.y - convert_p2wDist(semiMajorAxis);
            angle = 0f;
            period = (float) Math.sqrt(Math.pow(semiMajorAxis,3d) / star.mass);
            //circle = new Circle(position.x-pxradius/2, position.y-pxradius/2, pxradius);
            circle = new Circle(0f, 0f, pxradius);
            advance(0f);
        }

        private void advance(float dt){
            // advance the planets position
            //distanceMoved = convert_p2wDist(2f * (float) Math.PI * semiMajorAxis * (convert_w2pTime(dt) / period));
            //angle = star.getAngleTo(position);
            //position.x += (float) Math.cos(Math.toRadians(angle)) * distanceMoved;
            //position.y += (float) Math.sin(Math.toRadians(angle)) * distanceMoved;
            angle += 360f * (convert_w2pTime(dt) / period);
            position.x = star.position.x + convert_p2wDist(semiMajorAxis) * (float) Math.cos(Math.toRadians(angle));
            position.y = star.position.y + convert_p2wDist(semiMajorAxis) * (float) Math.sin(Math.toRadians(angle));
            circle.setX(position.x);
            circle.setY(position.y);

        }
    }

    private void spawnProbe(float launchAngle, float launchSpeed){

        // define position and velocity vectors
        Vector3 position = new Vector3(shipX + shipWidth/2 - probeImage.getWidth()/2, shipY+shipHeight/2 - probeImage.getHeight()/2, 0);
        Vector3 velocity = new Vector3(-1f * (float) Math.sin(Math.toRadians(launchAngle)) * launchSpeed,
                (float) Math.cos(Math.toRadians(launchAngle)) * launchSpeed, 0);

        Probe probe = new Probe(probeImage, position, velocity);
        probes.add(probe);

    }

    public GameScreen(final PlanetProbe gam) {
        this.game = gam;

        // some helpful display variable
        screenAspectRatio = 1f*game.screenHeight / game.screenWidth; // not used yet
        shipWidth = 32;
        shipHeight = 32;
        shipY = 32;
        shipX = game.screenWidth / 2 - shipWidth / 2;

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

        // world-to-physical (w2p) and physical-to-world (p2w) converters
        sunEarthAcceleration = 39.42f; // AU year^-2
        p2wDist = (2f * game.screenWidth);
        w2pDist = 1f / p2wDist;
        p2wTime = 180f;
        w2pTime = 1f / p2wTime;

        // scalar between probe launch line in pixels and launch speed in pixels per second
        probeLaunchTouchScale = 0.5f;

        // grab phone aspect ratio -- not used yet
        phoneAspectRatio = 1f* Gdx.graphics.getHeight() / Gdx.graphics.getWidth();

        // create camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.screenWidth, game.screenHeight);

        // create our sprite batch and shape render
        shapeRenderer = new ShapeRenderer();

        // create our star
        star = new Star(1f, 8);

        // and our planets
        planets = new Array<Planet>();
        // add three planets with random masses (1e-2 - 1e-6 solar masses)
        // and random semimajor axes (0.01 - 0.25 AU).
        for (int i=0; i<3; i++){
            float pmass = (float) Math.pow(10,((Math.random() * (-2 - -6)) + -6));
            float psemiMajorAxis = (float) (Math.random() * (0.25f - 0.01f)) + 0.01f;
            int ppxradius = (int) Math.ceil((Math.log10(pmass)+6d));
            planets.add(new Planet(pmass, psemiMajorAxis, ppxradius));
            planetString += String.format("%.1e", pmass) + " "
                    + String.format("%.2f", psemiMajorAxis) + " "
                    + ppxradius + "\n";
        }
        //planets.add(new Planet(0.0005f, 0.1f, 3));
        //planets.add(new Planet(0.001f, 0.02f, 4));
        //planets.add(new Planet(1f-6, 0.2f, 2));

        // create all our image textures
        shipImage = new Texture(Gdx.files.internal("ship.png"));
        probeImage = new Texture(Gdx.files.internal("probe.png"));

        // create sprites to represent the ship
        ship = new Sprite(shipImage);
        ship.setPosition(shipX, shipY);

        // probes array
        probes = new Array<Probe>();

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

    @Override
    public void render(float delta) {

        // set background color and clear
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // time difference
        deltaTime = Gdx.graphics.getDeltaTime();

        // tell the camera to update its matrices.
        /*
        is this necessary if the camera is not changing?
        */
        //camera.update();

        // tell the SpriteBatch and ShapeRender to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // render our star
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(star.position.x, star.position.y, star.pxradius);
        for (Planet planet: planets){
            if (planet.probed)
                shapeRenderer.setColor(Color.RED);
            else
                shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.circle(planet.position.x, planet.position.y, planet.pxradius);
        }
        shapeRenderer.end();

        // batch draw everything
        game.batch.begin();
        //font.draw(batch, probeString, 10, screenHeight-10);
        game.font.draw(game.batch, planetString, game.screenWidth-100, game.screenHeight-10, 100, Align.left, false);
        game.bigfont.draw(game.batch, "DEMO", 5, game.screenHeight-10);
        ship.draw(game.batch);
        for(Probe probe: probes) {
            probe.sprite.draw(game.batch);
        }
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

        //move planets to next position
        if (planets != null) {
            Iterator<Planet> iter = planets.iterator();
            while (iter.hasNext()) {
                Planet planet = iter.next();

                planet.advance(deltaTime);
            }
        }

        // move probes to next position
        if (probes != null) {
            Iterator<Probe> iter = probes.iterator();
            while(iter.hasNext()) {
                Probe probe = iter.next();

                // accelerate the probe
                Vector3 accel = star.getAccelerationWorld(probe.position);
                for (Planet planet: planets)
                    accel.add(planet.getAccelerationWorld(probe.position));

                probe.velocity.x += accel.x * deltaTime;
                probe.velocity.y += accel.y * deltaTime;


                //get direction of probe and move forward in that direction
                float cosAng = (float) Math.cos(Math.toRadians(probe.getAngle()));
                float sinAng = (float) Math.sin(Math.toRadians(probe.getAngle()));
                float vel = probe.velocity.len();
                probe.advance(-1f*sinAng * vel * deltaTime, cosAng * vel * deltaTime);

                // remove the probe if it leaves the screen
                if (probe.sprite.getY() > game.screenHeight || probe.sprite.getY() < 0f || probe.sprite.getX() > game.screenWidth || probe.sprite.getX() < 0)
                    iter.remove();

                // remove the probe if it hits the star
                if (Intersector.overlaps(star.circle,probe.sprite.getBoundingRectangle()))
                    iter.remove();

                for (Planet planet: planets){
                    if (Intersector.overlaps(planet.circle,probe.sprite.getBoundingRectangle())) {
                        iter.remove();
                        planet.probed = true;
                    }
                }

                /*
                probeString =   probe.velocity.x + " " + probe.velocity.y + "\n"
                              + probe.getAngle() + "\n"
                              + accel + "\n"
                              + star.getAngleTo(probe.position) + "\n"
                              + star.position + "\n"
                              + probe.position;
                */
            }
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
