package com.fteams.siftrain.util.random;

import com.badlogic.gdx.utils.Array;
import com.fteams.siftrain.objects.CircleMark;

import java.util.ArrayList;
import java.util.List;

public abstract class Randomizer {
    public abstract void randomize(Array<CircleMark> marks);

    protected double calculateAverageDistance(Array<CircleMark> marks) {

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
}
