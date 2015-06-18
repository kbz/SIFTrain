package com.fteams.siftrain.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GlobalConfiguration {
    public static int songVolume;
    public static int feedbackVolume;
    // team strength in SIF units
    public static int teamStrength;
    // offset in milliseconds

    public static int offset;
    // path to beatmaps
    public static String pathToBeatmaps;

    public static void loadConfiguration()
    {
        Preferences prefs = Gdx.app.getPreferences("sif_train_config");
        teamStrength = prefs.getInteger("team_str", 20000);
        offset = prefs.getInteger("offset", 0);
        songVolume = prefs.getInteger("song_vol", 100);
        feedbackVolume = prefs.getInteger("feedback_vol", 100);
        pathToBeatmaps = prefs.getString("path_to_beatmaps", Gdx.files.getExternalStoragePath() + "beatmaps");

    }

    public static void storeConfiguration(){
        Preferences prefs = Gdx.app.getPreferences("sif_train_config");
        prefs.putInteger("team_str", teamStrength);
        prefs.putInteger("offset", offset);
        prefs.putInteger("song_vol", songVolume);
        prefs.putInteger("feedback_vol", feedbackVolume);
        prefs.putString("path_to_beatmaps", pathToBeatmaps);
        prefs.flush();
    }
}
