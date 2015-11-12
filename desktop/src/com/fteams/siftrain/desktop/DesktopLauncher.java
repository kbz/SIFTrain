package com.fteams.siftrain.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fteams.siftrain.SifTrain;
import com.fteams.siftrain.assets.GlobalConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		GlobalConfiguration.appVersionName = "debug build.";
		new LwjglApplication(new SifTrain(), "SIF Train", 1280, 720);
	}
}
