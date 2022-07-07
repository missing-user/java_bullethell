package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MainGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.foregroundFPS = 300;
		config.backgroundFPS = 1;
		config.vSyncEnabled = true;
		config.useGL30 = true;
		//config.fullscreen = true;
		LwjglApplication lwgl = new LwjglApplication(new MainGame(), config);
	}
}
