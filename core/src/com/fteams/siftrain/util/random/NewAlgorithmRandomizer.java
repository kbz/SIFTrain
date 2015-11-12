package com.fteams.siftrain.util.random;

import com.badlogic.gdx.utils.Array;
import com.fteams.siftrain.assets.GlobalConfiguration;
import com.fteams.siftrain.objects.CircleMark;
import com.fteams.siftrain.util.SongUtils;
import com.sun.media.sound.SoftEnvelopeGenerator;

public class NewAlgorithmRandomizer extends Randomizer {

    private boolean left;
    private boolean holding;
    private double holdEndTime;
    // 5 ms of buffer to prevent simultaneous notes on the same side

    /*
     * Info: by default beatmaps will use 2 notes at the same time at most. If there's more, things can get nasty and messy.
     */
    public void randomize(Array<CircleMark> marks) {
        marks.sort();

        double threshold = SongUtils.getDefaultNoteSpeedForApproachRate(GlobalConfiguration.noteSpeed) / 4.0;

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
                randomizeHold(marks, i, isLeft, threshold);
            } else
            // not a hold
            {
                randomizeNotHold(marks, i, isLeft, threshold);
            }
        }
    }

    private void randomizeHold(Array<CircleMark> marks, int i, boolean isLeft, double threshold) {
        CircleMark mark = marks.get(i);

        // we're holding!
        if (holding) {
            // note during hold which ends after the current end time
            // swap times.
            if (mark.getNote().timing_sec + mark.getNote().effect_value > holdEndTime) {
                holdEndTime = mark.getNote().timing_sec + mark.getNote().effect_value + BUFFER_TIME;
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
            double previousHoldEndTime = holdEndTime;
            holdEndTime = mark.getNote().timing_sec + mark.getNote().effect_value + BUFFER_TIME;

            // check the previous note for side calculation - just in case
            if (i >= 1) {
                CircleMark previous = marks.get(i - 1);
                // we're no longer holding, but the new hold spawns close to the previous released hold
                if (mark.getNote().timing_sec - previousHoldEndTime < threshold) {
                    left = !left;
                    Integer pos = getPositionWithoutMiddle(left);
                    mark.updateDestination(pos);
                    mark.left = left;

                }
                // notes close together must go to different sides
                else if (mark.getNote().timing_sec - previous.getNote().timing_sec < threshold) {
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

    private void randomizeNotHold(Array<CircleMark> marks, int i, boolean isLeft, double threshold) {
        CircleMark mark = marks.get(i);

        // we're holding
        if (holding) {
            // pick the other side
            isLeft = !left;
            // if one side is holding, notes will never spawn in the middle lane
            Integer pos = getPositionWithoutMiddle(isLeft, mark.getNote().timing_sec);
            mark.updateDestination(pos);
            mark.left = isLeft;
            // we're not holding
        } else {
            // and this is not the first note
            if (i >= 1) {
                CircleMark previous = marks.get(i - 1);
                // if the notes are close together, use different sides
                if (mark.getNote().timing_sec - previous.getNote().timing_sec < threshold) {
                    isLeft = !previous.left;
                    // if notes are too close together, we don't pick up the center
                    // this also applies to simultaneous notes (distance will obviously be < average)
                    Integer pos = getPositionWithoutMiddle(isLeft, mark.getNote().timing_sec);
                    mark.updateDestination(pos);
                    mark.left = isLeft;
                    // we're not holding but there was a hold released close - swap sides
                } else if (mark.getNote().timing_sec - holdEndTime < threshold) {
                    // they're far away and we may choose a side randomly
                    isLeft = !left;
                    System.out.println("TEST-O");
                    Integer pos = getPositionWithoutMiddle(isLeft, mark.getNote().timing_sec);
                    mark.updateDestination(pos);
                    mark.left = isLeft;
                    // if they're not really close, just pick a random side
                } else if (hasSimultEndBeforeCurrentNote(marks, i, i-1)) {
                    isLeft = !left;
                    System.out.println("TEST-O-O");
                    Integer pos = getPositionWithoutMiddle(isLeft, mark.getNote().timing_sec);
                    mark.updateDestination(pos);
                    mark.left = isLeft;

                } else {
                    System.out.println("TEST-O-O-O!");
                    Integer pos = getPosition(isLeft);
                    // if the note is multi - don't pick the center
                    if ((mark.effect & (SongUtils.NOTE_TYPE_SIMULT_END | SongUtils.NOTE_TYPE_SIMULT_START)) != 0) {
                        pos = getPositionWithoutMiddle(isLeft, mark.getNote().timing_sec);
                    }
                    mark.updateDestination(pos);
                    mark.left = isLeft;
                }
                // if this is the first note, just pick a random side
            } else {
                Integer pos = getPosition(isLeft, mark.getNote().timing_sec);
                // if the first note is a simult, choose a random side - no center
                if ((mark.effect & (SongUtils.NOTE_TYPE_SIMULT_START | SongUtils.NOTE_TYPE_SIMULT_END)) != 0)
                {
                    pos = getPositionWithoutMiddle(isLeft, mark.getNote().timing_sec);
                }
                mark.updateDestination(pos);
                mark.left = isLeft;
            }
        }
    }

    public Integer getPosition(boolean isLeft, Double timing) {
        Integer newPosition = getPosition(isLeft);
        // try to get a spot on one side
        if (hasSpots(isLeft, timing))
        {
            while (inUse(newPosition, timing)) {
                newPosition = getPosition(isLeft);
            }
        } else
        // if we couldn't get a spot because all are on cooldown, use a spot from the other side
        {
            if (hasSpots(!isLeft, timing)){
                while (inUse(newPosition, timing)) {
                    newPosition = getPosition(!isLeft);
                }
            }
            // if the spots are all in use, use the center.
            else
            {
                newPosition = 4;
            }
        }
        noteToReleaseTime.put(newPosition, timing + BUFFER_TIME);
        return newPosition;
    }

    public Integer getPositionWithoutMiddle(boolean isLeft, Double timing) {
        Integer newPosition = getPositionWithoutMiddle(isLeft);
        if (hasSpots(isLeft, timing))
        {
            while (inUse(newPosition, timing)) {
                newPosition = getPositionWithoutMiddle(isLeft);
            }
        } else
        {
            if (hasSpots(!isLeft, timing)){
                while (inUse(newPosition, timing)) {
                    newPosition = getPositionWithoutMiddle(!isLeft);
                }
            }
            else
            {
                newPosition = 5;
            }
        }
        noteToReleaseTime.put(newPosition, timing + BUFFER_TIME);
        return newPosition;
    }

}
