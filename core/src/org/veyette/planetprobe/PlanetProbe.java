package org.veyette.planetprobe;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PlanetProbe extends Game {
    public BitmapFont font;
    public BitmapFont bigfont;
    public SpriteBatch batch;
    public int screenWidth = 720;
    public int screenHeight = 1280;

    public void create() {
        batch = new SpriteBatch();

        // fonts
        font = new BitmapFont();
        font.setColor(0f, 0f, 0f, 1f);
        bigfont = new BitmapFont();
        bigfont.setColor(0f, 0f, 0f, 1f);
        bigfont.getData().setScale(3, 3);

        this.setScreen(new MainMenuScreen(this));
    }

    public void render() {
        super.render(); //important!
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
        bigfont.dispose();
    }

}