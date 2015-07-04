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

    public static String getDifficulty(Integer difficulty) {
        return DIFF[difficulty - 1];
    }

    public static Double getDefaultNoteSpeedForDifficulty(Integer difficulty) {
        switch(difficulty)
        {
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

    static float rankMultiplier [][] = {{1, 1.5f, 2, 2.5f},{1, 2, 2.5f, 3},{1, 2.5f, 3, 3.5f},{1, 2.75f, 3, 3.25f}};
    static float difficultyMultiplier [] = {88, 129, 170, 211};

    public static int getCScoreForSong(int size, Integer difficulty) {
        float rMult = rankMultiplier[difficulty-1][0];
        float dMult = difficultyMultiplier[difficulty-1];
        return (int)Math.ceil(rMult * dMult * size );
    }

    public static int getBScoreForSong(int size, Integer difficulty) {
        float rMult = rankMultiplier[difficulty-1][1];
        float dMult = difficultyMultiplier[difficulty-1];
        return (int)Math.ceil(rMult * dMult * size );
    }

    public static int getAScoreForSong(int size, Integer difficulty) {
        float rMult = rankMultiplier[difficulty-1][2];
        float dMult = difficultyMultiplier[difficulty-1];
        return (int)Math.ceil(rMult * dMult * size );
    }

    public static int getSScoreForSong(int size, Integer difficulty) {
        float rMult = rankMultiplier[difficulty-1][3];
        float dMult = difficultyMultiplier[difficulty-1];
        return (int)Math.ceil(rMult * dMult * size );
    }
}
