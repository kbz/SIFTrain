package com.fteams.siftrain.util.random;

import com.badlogic.gdx.utils.Array;
import com.fteams.siftrain.objects.CircleMark;
import com.fteams.siftrain.util.SongUtils;

public class SimpleRandomizer extends Randomizer {

    private double holdEndTime;
    private boolean left;

    /*
     * Info: by default beatmaps will use 2 notes at the same time at most. If there's more, things can get nasty and messy.
     */
    public void randomize(Array<CircleMark> marks) {
        marks.sort();

        double threshold = marks.get(0).speed / 4.0;

        // set the position for each note
        for (int i = 0; i < marks.size; i++) {
            CircleMark mark = marks.get(i);

            if (mark.hold) {
                // if the note is a hold, we store the ending time and ignore any notes which appear until the hold ends
                if (holdEndTime < mark.getNote().timing_sec + mark.getNote().effect_value) {
                    holdEndTime = mark.getNote().timing_sec + mark.getNote().effect_value;
                    left = isLeft(mark);
                    continue;
                }

            }

            // we give notes a bit of leeway for hold release to prevent having a note on the same side and almost simultaneously after the hold was released.
            if (mark.getNote().timing_sec < holdEndTime + threshold) {
                continue;
            }

            boolean isLeft = Math.random() > 0.5;

            // this note is a hold or a double don't randomize it
            if (!mark.hold && (mark.effect & (SongUtils.NOTE_TYPE_SIMULT_START | SongUtils.NOTE_TYPE_SIMULT_END)) == 0) {
                randomizeNotHold(marks, i, isLeft, threshold);
            }
        }
    }

    private void randomizeNotHold(Array<CircleMark> marks, int i, boolean isLeft, double threshold) {
        CircleMark mark = marks.get(i);

        // if this is not the first note
        if (i >= 1) {
            // we check against the previous note
            CircleMark previous = marks.get(i - 1);
            // if the notes are close together, use different sides
            if (mark.getNote().timing_sec - previous.getNote().timing_sec < threshold) {
                isLeft = !previous.left;
                // if notes are too close together, we don't pick up the center
                Integer pos = getPositionWithoutMiddle(isLeft);
                mark.updateDestination(pos);
                mark.left = isLeft;
            } else if (mark.getNote().timing_sec - holdEndTime < threshold) {
                // they're far away and we may choose a side randomly
                isLeft = !left;
                Integer pos = getPositionWithoutMiddle(isLeft);
                mark.updateDestination(pos);
                mark.left = isLeft;
                // if they're not really close, just pick a random side
            } else {
                Integer pos = getPosition(isLeft);
                mark.updateDestination(pos);
                mark.left = isLeft;
            }
            // if this is the first note, just pick a random side
        } else {
            Integer pos = getPosition(isLeft);
            mark.updateDestination(pos);
            mark.left = isLeft;
        }
    }
}
