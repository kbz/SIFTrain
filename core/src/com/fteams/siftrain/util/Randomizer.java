package com.fteams.siftrain.util;

import com.badlogic.gdx.utils.Array;
import com.fteams.siftrain.objects.CircleMark;

import java.util.ArrayList;
import java.util.List;

public class Randomizer {

    private boolean left;
    private boolean holding;
    private double holdEndTime;

    /*
     * Info: by default beatmaps will use 2 notes at the same time at most. If there's more, things can get nasty and messy.
     */
    public void randomize(Array<CircleMark> marks) {
        marks.sort();

        double averageDistance = calculateAverageDistance(marks);
        int processed = 0;

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
                Integer pos = getPositionForHold(left);
                mark.updateDestination(pos);
                mark.left = left;
                // new hold is shorter than the previous one
                // just add it.
            } else {
                isLeft = !left;
                Integer pos = getPositionForHold(isLeft);
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
                    Integer pos = getPositionForHold(isLeft);
                    mark.updateDestination(pos);
                    mark.left = isLeft;
                    left = isLeft;
                    // they're far away and we may choose a side randomly
                } else {
                    isLeft = !left;
                    Integer pos = getPositionForHold(isLeft);
                    mark.updateDestination(pos);
                    mark.left = isLeft;
                    left = isLeft;

                }
                // if the note is the first one, we just pick a random side and spawn it there.
            } else {
                left = isLeft;
                Integer pos = getPositionForHold(left);
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
            Integer pos = getPosition(isLeft);
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
                    Integer pos = getPosition(isLeft);
                    while (Math.abs(pos - previous.destination) <= 1) {
                        pos = getPosition(isLeft);
                    }
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

    private Integer getPosition(boolean isLeft) {
        // left = 4-8
        if (isLeft) {
            return 4 + (int) (Math.random() * 100) % 5;
        } else
            // right = 0-4
            return (int) (Math.random() * 100) % 5;
    }

    // holds don't spawn in the middle
    private Integer getPositionForHold(boolean isLeft) {
        // left = 5-8
        if (isLeft) {
            return 5 + (int) (Math.random() * 100) % 4;
        } else
            // right = 0-3
            return (int) (Math.random() * 100) % 4;

    }

}
