package net.bryanhaley.butterblitz.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.bryanhaley.butterblitz.GameMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280; config.height = 720; config.title = "Butter Blitzkrieg";
		new LwjglApplication(new GameMain(), config);
	}
}
