package com.fteams.siftrain.util;

import java.util.Random;

public class Tips {
    public static final String [] tips = {
            "Do you feel you're having ease when clearing songs? Try increasing the Overall Difficulty in the Settings > Timing settings.",
            "The game feels slow? Try increasing the Approach Rate in the Settings > Timing settings.",
            "Did you know that you can load new beatmaps without having to restart the game? Go into Settings > Other > Reload Beatmaps!",
            "Can't quite get the beat of the song? Try enabling hint sounds in Settings > Volume settings.",
            "Do you feel the timing window is too small? Try decreasing the Overall Difficulty in the Settings > Timing settings.",
            "If you can't react fast enough to tap on time, try decreasing the Approach Rate in the Settings > Timing settings.",
            "If the notes slow down and speed up, teleport, or stutter, try changing the Sync mode in Settings > Timing settings.",
            "Did you know that you can sort the song list in the song selection screen? Check Settings > Other. ",
            "Did you know that you can play a small fragment of the song in the A-B Repeat mode?\nWarning: There may be sound sync problems, try different timestamps until something sticks!",
            "Did you know that you can change the speed of a song to practice it in slo-mo? Try the changing the playback rate option after selecting a song.\nNote: Due to library limitations, music playback rate can't be changed so these attempts will have no music.",
            "Did you notice there's no ads in this app?",
            "If you've enjoyed using this app, why not rate it on Google Play?"
    };


    public static String getRandomTip()
    {
        Random random = new Random(System.nanoTime());
        return "Tip: " + tips[Math.abs(random.nextInt())%tips.length];
    }
}
