package org.veyette.planetprobe.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import org.veyette.planetprobe.PlanetProbe;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Planet Probe";
        config.width = 480;
        config.height = 800;
        new LwjglApplication(new PlanetProbe(), config);
    }
}
