package com.fteams.siftrain.util.random;

import com.badlogic.gdx.utils.Array;
import com.fteams.siftrain.objects.CircleMark;
import com.fteams.siftrain.util.SongUtils;

public class NewAlgorithmRandomizer extends Randomizer {

    private boolean left;
    private boolean holding;
    private double holdEndTime;

    /*
     * Info: by default beatmaps will use 2 notes at the same time at most. If there's more, things can get nasty and messy.
     */
    public void randomize(Array<CircleMark> marks) {
        marks.sort();

        double averageDistance = calculateAverageDistance(marks);

        holding = false;
        // set the position for each note
        for (int i = 0; i < marks.size; i++) {
            CircleMark mark = marks.get(i);
            if (mark.getNote().timing_sec > holdEndTime) {
                holding = false;
            }
            boolean isLeft = Math.random() > 0.5;

            // this note is a hold
            if (mark.hold) {
                randomizeHold(marks, i, isLeft, averageDistance);
            } else
            // not a hold
            {
                randomizeNotHold(marks, i, isLeft, averageDistance);
            }
        }
    }

    private void randomizeHold(Array<CircleMark> marks, int i, boolean isLeft, double averageDistance) {
        CircleMark mark = marks.get(i);

        // we're holding!
        if (holding) {
            // note during hold which ends after the current end time
            // swap times.
            if (mark.getNote().timing_sec + mark.getNote().effect_value > holdEndTime) {
                holdEndTime = mark.getNote().timing_sec + mark.getNote().effect_value;
                left = !left;
                Integer pos = getPositionWithoutMiddle(left);
                mark.updateDestination(pos);
                mark.left = left;
                // new hold is shorter than the previous one
                // just add it.
            } else {
                isLeft = !left;
                Integer pos = getPositionWithoutMiddle(isLeft);
                mark.updateDestination(pos);
                mark.left = isLeft;
            }
            // we got a hold and we're not holding
        } else {
            holding = true;
            holdEndTime = mark.getNote().timing_sec + mark.getNote().effect_value;

            // check the previous note for side calculation - just in case
            if (i >= 1) {
                CircleMark previous = marks.get(i - 1);
                // notes close together must go to different sides
                if (mark.getNote().timing_sec - previous.getNote().timing_sec < averageDistance) {
                    isLeft = !previous.left;
                    Integer pos = getPositionWithoutMiddle(isLeft);
                    mark.updateDestination(pos);
                    mark.left = isLeft;
                    left = isLeft;
                    // if the new note is released really close to the last hold, make sure to release on the other side
                } else {
                    isLeft = !left;
                    Integer pos = getPositionWithoutMiddle(isLeft);
                    mark.updateDestination(pos);
                    mark.left = isLeft;
                    left = isLeft;

                }
                // if the note is the first one, we just pick a random side and spawn it there.
            } else {
                left = isLeft;
                Integer pos = getPositionWithoutMiddle(left);
                mark.updateDestination(pos);
                mark.left = isLeft;
                left = isLeft;
            }
        }
    }

    private void randomizeNotHold(Array<CircleMark> marks, int i, boolean isLeft, double averageDistance) {
        CircleMark mark = marks.get(i);

        // we're holding
        if (holding) {
            // pick the other side
            isLeft = !left;
            // if one side is holding, notes will never spawn in the middle lane
            Integer pos = getPositionWithoutMiddle(isLeft);
            mark.updateDestination(pos);
            mark.left = isLeft;
            // we're not holding
        } else {
            // and this is not the first note
            if (i >= 1) {
                CircleMark previous = marks.get(i - 1);
                // if the notes are close together, use different sides
                if (mark.getNote().timing_sec - previous.getNote().timing_sec < averageDistance) {
                    isLeft = !previous.left;
                    // if notes are too close together, we don't pick up the center
                    // this also applies to simultaneous notes (distance will obviously be < average)
                    Integer pos = getPositionWithoutMiddle(isLeft);
                    mark.updateDestination(pos);
                    mark.left = isLeft;
                } else if (mark.getNote().timing_sec - holdEndTime < averageDistance) {
                    // they're far away and we may choose a side randomly
                    isLeft = !left;
                    Integer pos = getPositionWithoutMiddle(isLeft);
                    mark.updateDestination(pos);
                    mark.left = isLeft;
                    // if they're not really close, just pick a random side
                }else {
                    Integer pos = getPosition(isLeft);
                    // if the note is multi - don't pick the center
                    if ((mark.effect & (SongUtils.NOTE_TYPE_SIMULT_END | SongUtils.NOTE_TYPE_SIMULT_START)) != 0)
                    {
                        pos = getPositionWithoutMiddle(isLeft);
                    }
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
}
