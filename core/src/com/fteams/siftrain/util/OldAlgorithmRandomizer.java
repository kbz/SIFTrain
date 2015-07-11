package com.fteams.siftrain.util;

import com.badlogic.gdx.utils.Array;
import com.fteams.siftrain.objects.CircleMark;

import java.util.ArrayList;
import java.util.List;

public class OldAlgorithmRandomizer {

    private double holdEndTime;

    /*
     * Info: by default beatmaps will use 2 notes at the same time at most. If there's more, things can get nasty and messy.
     */
    public void randomize(Array<CircleMark> marks) {
        // sort marks by timing
        marks.sort();
        double averageDistance = calculateAverageDistance(marks);

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

    private double calculateAverageDistance(Array<CircleMark> marks) {

        List<Double> distances = new ArrayList<>();
        for (int i = 0; i < marks.size - 1; i++) {
            if (marks.get(i).getNote().timing_sec.equals(marks.get(i + 1).getNote().timing_sec))
                continue;
            distances.add(Math.abs(marks.get(i + 1).getNote().timing_sec - marks.get(i).getNote().timing_sec));
        }
        double averageDistance = 0.0;
        for (Double distance : distances) {
            averageDistance += distance;
        }
        averageDistance = averageDistance / distances.size();
        return averageDistance;
    }

    public Integer getRandomPosition() {
        return (int)(Math.random() * 100) % 9;
    }
}
