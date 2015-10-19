package com.fteams.siftrain.assets;

import com.fteams.siftrain.objects.CircleMark.Accuracy;
import com.fteams.siftrain.util.SongUtils;

public class Results {
    public static Integer combo;
    public static float accuracy;
    public static int miss;
    public static int bads;
    public static int goods;
    public static int greats;
    public static int perfects;
    public static float maxAccuracy;
    public static float minAccuracy;
    public static float normalizedAccuracy;
    public static float unstableRating;

    public static float getAccuracyMultiplierForAccuracy(Accuracy accuracy) {
        if (accuracy == Accuracy.PERFECT) {
            return 1.0f;
        }
        if (accuracy == Accuracy.GREAT) {
            return 0.75f;
        }
        if (accuracy == Accuracy.GOOD) {
            return 0.50f;
        }
        if (accuracy == Accuracy.BAD) {
            return 0.25f;
        }
        return 0f;
    }

    public static Accuracy getAccuracyFor(float timing) {
        // Perfect
        if (Math.abs(timing) < SongUtils.overallDiffPerfect[GlobalConfiguration.overallDifficulty] / 1000) {
            return Accuracy.PERFECT;
        }
        if (Math.abs(timing) < SongUtils.overallDiffGreat[GlobalConfiguration.overallDifficulty]/ 1000) {
            return Accuracy.GREAT;
        }
        if (Math.abs(timing) < SongUtils.overallDiffNice[GlobalConfiguration.overallDifficulty]/ 1000) {
            return Accuracy.GOOD;
        }
        if (Math.abs(timing) < SongUtils.overallDiffBad[GlobalConfiguration.overallDifficulty]/ 1000) {
            return Accuracy.BAD;
        }
        return Accuracy.MISS;
    }

    public static void clear() {
        combo = 0;
        accuracy = 0;
        miss = 0;
        bads = 0;
        goods = 0;
        greats = 0;
        perfects = 0;
        maxAccuracy = 0;
        minAccuracy = 0;
        normalizedAccuracy = 0;
        unstableRating = 0;
    }
}
