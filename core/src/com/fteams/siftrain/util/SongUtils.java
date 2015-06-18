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

}
