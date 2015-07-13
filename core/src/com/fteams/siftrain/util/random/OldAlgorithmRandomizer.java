package com.fteams.siftrain.util.random;

import com.badlogic.gdx.utils.Array;
import com.fteams.siftrain.objects.CircleMark;
import com.fteams.siftrain.util.SongUtils;

public class OldAlgorithmRandomizer extends Randomizer {

    private double holdEndTime;

    /*
     * Info: by default beatmaps will use 2 notes at the same time at most. If there's more, things can get nasty and messy.
     */
    public void randomize(Array<CircleMark> marks) {
        // sort marks by timing
        marks.sort();

        double averageDistance = marks.get(0).speed / 4.0;

        // set the position for each note
        for (int i = 0; i < marks.size; i++) {
            CircleMark mark = marks.get(i);
            if (mark.hold)
            {
                // if the note is a hold, we store the ending time and ignore any notes which appear until the hold ends
                if (holdEndTime < mark.getNote().timing_sec + mark.getNote().effect_value)
                {
                    holdEndTime = mark.getNote().timing_sec + mark.getNote().effect_value;
                    continue;
                }

            }

            // we give notes a bit of leeway for hold release to prevent having a note on the same side and almost simultaneously after the hold was released.
            if (mark.getNote().timing_sec < holdEndTime + averageDistance)
            {
                continue;
            }

            // this note is a hold or a double don't randomize it
            if (!mark.hold && (mark.effect & (SongUtils.NOTE_TYPE_SIMULT_START | SongUtils.NOTE_TYPE_SIMULT_END)) == 0 ) {
                Integer pos = getRandomPosition();
                mark.updateDestination(pos);
            }
        }
    }

}
