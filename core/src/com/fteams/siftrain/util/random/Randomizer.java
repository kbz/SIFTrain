package com.fteams.siftrain.util.random;

import com.badlogic.gdx.utils.Array;
import com.fteams.siftrain.objects.CircleMark;

public abstract class Randomizer {

    static final double BUFFER_TIME = 0.005d;

    public abstract void randomize(Array<CircleMark> marks);

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
}
