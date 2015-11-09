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
    public static int noteSpeed;
    public static int overallDifficulty;
    // path to beatmaps
    public static String pathToBeatmaps;
    public static boolean playHintSounds;
    // sorting related
    public static int sortMode;
    public static int sortOrder;
    // randomizer config
    public static int randomMode;
    // sync config
    public static int syncMode;

    // other - per session configs
    public static boolean random;

    // not stored/remembered variables:
    public static Float playbackRate;
    public static Integer playbackMode;
    public static Float aTime;
    public static Float bTime;

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
        noteSpeed = prefs.getInteger("note_speed", 6);
        overallDifficulty = prefs.getInteger("overall_difficulty", 7);
        // default to song name sorting
        sortMode = prefs.getInteger("sorting_mode", SongUtils.SORTING_MODE_SONG_NAME);
        sortOrder = prefs.getInteger("sorting_order", SongUtils.SORTING_MODE_ASCENDING);
        // default to the new mode
        randomMode = prefs.getInteger("random_mode", SongUtils.RANDOM_MODE_NEW);
        // sync mode
        syncMode = prefs.getInteger("sync_mode", SongUtils.SYNC_MODE_1);

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
        prefs.putInteger("note_speed", noteSpeed);
        prefs.putInteger("overall_difficulty", overallDifficulty);
        prefs.putInteger("sorting_mode", sortMode);
        prefs.putInteger("random_mode", randomMode);
        prefs.putInteger("sorting_order", sortOrder);
        prefs.putInteger("sync_mode", syncMode);
        prefs.flush();
    }
}
