package com.fteams.siftrain.assets;

import com.fteams.siftrain.objects.CircleMark.Accuracy;

public class Results {
    public static Integer score;
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

    public static Accuracy getAccuracyFor(float timing, double noteSpeed) {
        // Perfect
        if (Math.abs(timing) < noteSpeed * 0.05f) {
            return Accuracy.PERFECT;
        }
        if (Math.abs(timing) < noteSpeed * 0.15f) {
            return Accuracy.GREAT;
        }
        if (Math.abs(timing) < noteSpeed * 0.20f) {
            return Accuracy.GOOD;
        }
        if (Math.abs(timing) < noteSpeed * 0.5f) {
            return Accuracy.BAD;
        }
        return Accuracy.MISS;
    }

    public static float getMultiplierForAccuracy(Accuracy accuracy) {
        if (accuracy == Accuracy.PERFECT) {
            return 1.0f;
        }
        if (accuracy == Accuracy.GREAT) {
            return 0.88f;
        }
        if (accuracy == Accuracy.GOOD) {
            return 0.8f;
        }
        if (accuracy == Accuracy.BAD) {
            return 0.4f;
        }
        return 0f;
    }

    public static float getMultiplierForCombo(int combo) {

        if (combo <= 50) {
            return 1.00f;
        } else if (50 < combo && combo <= 100) {
            return 1.10f;
        } else if (100 < combo && combo <= 200) {
            return 1.15f;
        } else if (200 < combo && combo <= 400) {
            return 1.20f;
        } else if (400 < combo && combo <= 600) {
            return 1.25f;
        } else if (600 < combo && combo <= 800) {
            return 1.30f;
        } else {
            return 1.35f;
        }
    }

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

    public static String getRankString() {
        // no rank
        if (score < Assets.selectedSong.rank_info[0].rank_max) {
            return "No Rank";
        }
        if (score < Assets.selectedSong.rank_info[1].rank_max) {
            return "C Rank";
        }
        if (score < Assets.selectedSong.rank_info[2].rank_max) {
            return "B Rank";
        }
        if (score < Assets.selectedSong.rank_info[3].rank_max) {
            return "A Rank";
        }
        return "S rank";
    }

    public static void clear() {
        score = 0;
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
