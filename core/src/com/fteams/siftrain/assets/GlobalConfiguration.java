package com.fteams.siftrain.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.fteams.siftrain.util.SongUtils;

public class GlobalConfiguration {
    public static int songVolume;
    public static int feedbackVolume;
    // team strength in SIF units
    public static int teamStrength;
    // offset in milliseconds
    public static int offset;
    public static int inputOffset;
    // path to beatmaps
    public static String pathToBeatmaps;
    public static boolean playHintSounds;
    // sorting related
    public static int sortMode;
    // randomizer config
    public static int randomMode;
    // sync config
    public static int syncMode;
    public static float speedMultiplier;

    // other - per session configs
    public static boolean random;

    public final static int BASE_HEIGHT = 720;

    public static void loadConfiguration() {
        Preferences prefs = Gdx.app.getPreferences("sif_train_config");
        teamStrength = prefs.getInteger("team_str", 20000);
        offset = prefs.getInteger("offset", 0);
        inputOffset = prefs.getInteger("input_offset", 0);
        songVolume = prefs.getInteger("song_vol", 100);
        feedbackVolume = prefs.getInteger("feedback_vol", 100);
        pathToBeatmaps = prefs.getString("path_to_beatmaps", Gdx.files.getExternalStoragePath() + "beatmaps");
        playHintSounds = prefs.getBoolean("play_hint_sounds", false);
        // default to song name sorting
        sortMode = prefs.getInteger("sorting_mode", SongUtils.SORTING_MODE_SONG_NAME);
        // default to the new mode
        randomMode = prefs.getInteger("random_mode", SongUtils.RANDOM_MODE_NEW);
        // sync mode
        syncMode = prefs.getInteger("sync_mode", SongUtils.SYNC_MODE_1);
        speedMultiplier = prefs.getFloat("speed_multiplier", 1f);
    }

    public static void storeConfiguration() {
        Preferences prefs = Gdx.app.getPreferences("sif_train_config");
        prefs.putInteger("team_str", teamStrength);
        prefs.putInteger("offset", offset);
        prefs.putInteger("input_offset", inputOffset);
        prefs.putInteger("song_vol", songVolume);
        prefs.putInteger("feedback_vol", feedbackVolume);
        prefs.putString("path_to_beatmaps", pathToBeatmaps);
        prefs.putBoolean("play_hint_sounds", playHintSounds);
        prefs.putInteger("sorting_mode", sortMode);
        prefs.putInteger("random_mode", randomMode);
        prefs.putInteger("sync_mode", syncMode);
        prefs.putFloat("speed_multiplier", speedMultiplier);
        prefs.flush();
    }
}
