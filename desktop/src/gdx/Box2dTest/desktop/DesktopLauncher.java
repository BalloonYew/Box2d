package gdx.Box2dTest.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import gdx.Box2dTest.GdxBox2d;

public class DesktopLauncher {

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 720;
        config.height = 480;
        config.backgroundFPS = 60;
        config.foregroundFPS = 60;
        new LwjglApplication(new GdxBox2d(), config);
    }
}
