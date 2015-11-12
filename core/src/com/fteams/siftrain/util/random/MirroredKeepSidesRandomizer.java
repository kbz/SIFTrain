package com.fteams.siftrain.util.random;

import com.badlogic.gdx.utils.Array;
import com.fteams.siftrain.objects.CircleMark;

public class MirroredKeepSidesRandomizer extends Randomizer {

    /*
     * Info: by default beatmaps will use 2 notes at the same time at most. If there's more, things can get nasty and messy.
     */
    public void randomize(Array<CircleMark> marks) {
        // sort marks by timing
        marks.sort();

        for (CircleMark mark : marks)
        {
            // we don't process notes which spawn in the middle
            if (mark.getNote().position == 5)
            {
                continue;
            }
            boolean left = isLeft(mark);
            Integer newPosition = getPositionWithoutMiddle(!left);
            while (inUse(newPosition, mark.getNote().timing_sec))
            {
                newPosition = getPosition(!left);
            }
            mark.updateDestination(newPosition);
            noteToReleaseTime.put(newPosition, mark.getNote().timing_sec + BUFFER_TIME);
        }
    }
}
