package com.fteams.siftrain.util.random;

import com.badlogic.gdx.utils.Array;
import com.fteams.siftrain.objects.CircleMark;

import java.util.HashMap;
import java.util.Map;

public class ExtremeRandomizer extends Randomizer {

    private Map<Integer, Double> noteToReleaseTime = new HashMap<>();

    /*
     * Info: this is the only randomizer which works with ANY BEATMAP (even those with more than 2 notes at the same time!)
     */
    public void randomize(Array<CircleMark> marks) {
        // sort marks by timing
        marks.sort();

        for (int i = 0 ; i < marks.size; i++)
        {
            CircleMark mark = marks.get(i);
            Integer pos = getFreePosition(mark.getNote().timing_sec);
            noteToReleaseTime.put(pos, mark.getNote().timing_sec + (mark.hold ? mark.getNote().effect_value : 0));
            mark.updateDestination(pos);
        }
    }

    private Integer getFreePosition(Double timing_sec) {
        Integer position = (int)(Math.random() * 100) % 9;
        while (noteToReleaseTime.get(position) != null && noteToReleaseTime.get(position) >= timing_sec )
        {
            position = (int)(Math.random() * 100) % 9;
        }
        return position;
    }

}
