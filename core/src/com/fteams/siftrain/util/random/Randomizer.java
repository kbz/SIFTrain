package com.fteams.siftrain.util.random;

import com.badlogic.gdx.utils.Array;
import com.fteams.siftrain.objects.CircleMark;
import com.fteams.siftrain.util.SongUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class Randomizer {

    static final double BUFFER_TIME = 0.005d;

    public abstract void randomize(Array<CircleMark> marks);

    protected Map<Integer, Double> noteToReleaseTime = new HashMap<>();

    protected Integer getPosition(boolean isLeft) {
        // left = 4-8
        if (isLeft) {
            return 4 + (int) (Math.random() * 100) % 5;
        } else
            // right = 0-4
            return (int) (Math.random() * 100) % 5;
    }

    // holds don't spawn in the middle
    protected Integer getPositionWithoutMiddle(boolean isLeft) {
        // left = 5-8
        if (isLeft) {
            return 5 + (int) (Math.random() * 100) % 4;
        } else
            // right = 0-3
            return (int) (Math.random() * 100) % 4;
    }

    protected Integer getRandomPosition() {
        return (int)(Math.random() * 100) % 9;
    }

    protected boolean isLeft(CircleMark mark) {
        return mark.getNote().position > 5;
    }

    protected Boolean hasSimultEndBeforeCurrentNote(Array<CircleMark> marks, Integer currentIndex, Integer lookupIndex)
    {
        CircleMark lookup = marks.get(lookupIndex);
        Double lookupStartTime = lookup.getNote().timing_sec;
        Double lookupHoldEndTime = lookup.getNote().timing_sec + lookup.getNote().effect_value;

        CircleMark current = marks.get(currentIndex);
        Double currentTime = current.getNote().timing_sec;

        for (int i = 0; i < marks.size; i++)
        {
            if (i == currentIndex)
                continue;

            if (i == lookupIndex)
                continue;

            CircleMark mark = marks.get(i);
            if (mark.getNote().timing_sec >= currentTime)
                continue;
            // if both are holds, we only check ends
            // if one is a hold, we check ends vs tap
            // if both are taps we check both
            if (lookup.hold)
            {
                // 2 holds
                if (mark.hold)
                {
                    if (mark.getNote().timing_sec + mark.getNote().effect_value == lookupHoldEndTime)
                    {
                        return true;
                    }
                }
                else
                {
                    // 1 hold 1 tap
                    if (mark.getNote().timing_sec.equals(lookupHoldEndTime))
                    {
                        return true;
                    }
                }
            }
            else
            {
                // 1 hold 1 tap
                if (mark.hold)
                {
                    if (mark.getNote().timing_sec + mark.getNote().effect_value == lookupStartTime)
                    {
                        return true;
                    }
                }
                else
                {
                    // 2 taps
                    if (mark.getNote().timing_sec.equals(lookupStartTime))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected Boolean inUse(Integer position, Double timing_sec) {
        return noteToReleaseTime.get(position) != null && noteToReleaseTime.get(position) >= timing_sec;
    }

    protected Boolean hasSpots(Boolean isLeft, Double timing)
    {
        for (int i = isLeft ? 5 : 0; i <= (isLeft ? 8 : 3); i++)
        {
            if (noteToReleaseTime.get(i) == null || noteToReleaseTime.get(i) < timing)
                return true;
        }
        return false;
    }
}
