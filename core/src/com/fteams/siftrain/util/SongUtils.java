package com.fteams.siftrain.util;

public class SongUtils {
    public final static String[] DIFF = {"EASY", "NORMAL", "HARD", "EXPERT"};
    public final static Integer NOTE_TYPE_NORMAL = 0b00000001;
    public final static Integer NOTE_TYPE_TOKEN = 0b00000010;
    public final static Integer NOTE_TYPE_HOLD = 0b00000100;
    public final static Integer NOTE_TYPE_SPECIAL = 0b00001000;
    public final static Integer NOTE_TYPE_SIMULT_START = 0b00010000;
    public final static Integer NOTE_TYPE_SIMULT_END = 0b00100000;
    public final static Integer NOTE_TYPE_HOLD_END = 0b01000000;

    public final static Integer SORTING_MODE_FILE_NAME = 0;
    public final static Integer SORTING_MODE_SONG_NAME = 1;

    public final static Integer SORTING_MODE_ASCENDING = 0;
    public final static Integer SORTING_MODE_DESCENDING = 1;

    public final static Integer RANDOM_MODE_OLD = 0;
    public final static Integer RANDOM_MODE_NEW = 1;
    public final static Integer RANDOM_MODE_KEEP_SIDES = 2;
    public final static Integer RANDOM_MODE_MIRRORED_KEEP_SIDES = 3;

    public final static Integer SYNC_MODE_1 = 0;
    public final static Integer SYNC_MODE_2 = 1;
    public final static Integer SYNC_MODE_3 = 2;
    public final static Integer SYNC_DISABLED = 3;

    public final static Integer GAME_MODE_NORMAL = 0;
    public final static Integer GAME_MODE_ABREPEAT = 1;

    public final static String[] randomModes = {"Old mode", "New mode", "Keep Sides mode", "Mirrored Keep Sides mode", "Simple mode", "Extreme mode"};
    public final static String[] syncModes = {"Default", "Constant Sync", "Initial Sync", "Disabled"};

    public final static Long[] noteSpeeds = {1800L, 1680L, 1560L, 1440L, 1320L, 1200L, 1050L, 900L, 750L, 600L, 450L};
    public final static Double[] overallDiffPerfect = {79.5, 73.5, 67.5, 61.5, 56.5, 49.5, 43.5, 37.5, 31.5, 25.5, 19.5}; // -6
    public final static Double[] overallDiffGreat = {139.5, 131.5, 123.5, 115.5, 107.5, 99.5, 91.5, 83.5, 75.5, 67.5, 59.5}; // -8
    public final static Double[] overallDiffNice = {199.5, 189.5, 179.5, 169.5, 159.5, 149.5, 139.5, 129.5, 119.5, 109.5, 99.5}; // - 10
    public final static Double[] overallDiffBad = {249.5, 237.5, 225.5, 213.5, 201.5, 189.5, 177.5, 165.5, 153.5, 141.5, 129.5};// - 12

    public static Long getSpeedFromConfig(Integer noteSpeed) {
        return noteSpeeds[noteSpeed];
    }

    public static String getDifficulty(Integer difficulty) {
        return DIFF[difficulty - 1];
    }

    static double[] speedForApproachRate = {1.8, 1.68, 1.56, 1.44, 1.32, 1.2, 1.05, 0.9, 0.75, 0.6, 0.45};

    public static Double getDefaultNoteSpeedForApproachRate(int i) {
        return speedForApproachRate[i];
    }

    static int[][] positionForMode = {
            // 1:
            {5},
            // 2
            {6, 4},
            // 3
            {7, 5, 3},
            // 4
            {8, 6, 4, 2},
            // 5
            {7, 6, 5, 4, 3},
            // 6
            {8, 7, 6, 4, 3, 2},
            // 7
            {8, 7, 6, 5, 4, 3, 2},
            // 8
            {9, 8, 7, 6, 4, 3, 2, 1},
            // 9
            {9, 8, 7, 6, 5, 4, 3, 2, 1}
    };

    public static int getPositionForMode(int mode, int position) {
        return positionForMode[mode - 1][position];
    }
}
