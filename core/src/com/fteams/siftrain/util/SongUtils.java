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

    public final static Integer RANDOM_MODE_OLD = 0;
    public final static Integer RANDOM_MODE_NEW = 1;

    public static String getDifficulty(Integer difficulty) {
        return DIFF[difficulty - 1];
    }

    public static Double getDefaultNoteSpeedForDifficulty(Integer difficulty) {
        switch (difficulty) {
            case 1:
                return 1.6;
            case 2:
                return 1.3;
            case 3:
                return 1.0;
            case 4:
                return 0.8;
            default:
                return 1.0;
        }
    }

    static float rankMultiplier[][] = {{1, 1.5f, 2, 2.5f}, {1, 2, 2.5f, 3}, {1, 2.5f, 3, 3.5f}, {1, 2.75f, 3, 3.25f}};
    static float difficultyMultiplier[] = {88, 129, 170, 211};

    public static int getCScoreForSong(int size, Integer difficulty) {
        float rMult = rankMultiplier[difficulty - 1][0];
        float dMult = difficultyMultiplier[difficulty - 1];
        return (int) Math.ceil(rMult * dMult * size);
    }

    public static int getBScoreForSong(int size, Integer difficulty) {
        float rMult = rankMultiplier[difficulty - 1][1];
        float dMult = difficultyMultiplier[difficulty - 1];
        return (int) Math.ceil(rMult * dMult * size);
    }

    public static int getAScoreForSong(int size, Integer difficulty) {
        float rMult = rankMultiplier[difficulty - 1][2];
        float dMult = difficultyMultiplier[difficulty - 1];
        return (int) Math.ceil(rMult * dMult * size);
    }

    public static int getSScoreForSong(int size, Integer difficulty) {
        float rMult = rankMultiplier[difficulty - 1][3];
        float dMult = difficultyMultiplier[difficulty - 1];
        return (int) Math.ceil(rMult * dMult * size);
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
