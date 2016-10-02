package org.veyette.planetprobe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class MainMenuScreen implements Screen{
    final PlanetProbe game;

    OrthographicCamera camera;
    Sprite logoPlanet;
    Sprite logoText;
    Sprite touchText;
    Sprite logoRing;
    int screenWidth;
    int screenHeight;



    public MainMenuScreen(final PlanetProbe gam) {
        game = gam;
        logoPlanet = new Sprite(new Texture(Gdx.files.internal("logoplanet.png")));
        logoText = new Sprite(new Texture(Gdx.files.internal("logotext.png")));
        touchText = new Sprite(new Texture(Gdx.files.internal("logotext2.png")));
        logoRing = new Sprite(new Texture(Gdx.files.internal("logoring.png")));


        //logoPlanet.setScale(.2f);
        logoText.setScale(.9f);
        touchText.setScale(.7f);
        logoRing.setScale(.9f);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, gam.screenWidth, gam.screenHeight);


        screenWidth = game.screenWidth;
        screenHeight = game.screenHeight;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        //game.font.draw(game.batch, "Welcome to Planet Probe!!! ", 100, 150);
       // game.font.draw(game.batch, "Tap anywhere to begin!", 100, 100);
        //game.bigfont.draw(game.batch, "DEMO", 5, game.screenHeight-10);

        System.out.println();
        logoRing.setRotation(logoRing.getRotation() + 2*delta);


        logoRing.setPosition(screenWidth/2-logoRing.getWidth()/2,screenHeight/2-logoRing.getHeight()/2);
        logoText.setPosition(screenWidth/2-logoText.getWidth()/2,screenHeight/1.2f-logoText.getHeight()/2f);
        touchText.setPosition(screenWidth/2-touchText.getWidth()/2,screenHeight/6-touchText.getHeight()/2);
        logoPlanet.setPosition(screenWidth/2-logoPlanet.getWidth()/2,screenHeight/2-logoPlanet.getHeight()/2);
        logoPlanet.draw(game.batch);
        logoRing.draw(game.batch);
        logoText.draw(game.batch);
        touchText.draw(game.batch);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
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
    }
}