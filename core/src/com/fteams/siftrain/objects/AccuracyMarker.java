package com.fteams.siftrain.objects;

public class AccuracyMarker {
    // stay on screen for 5 seconds
    public float displayTime = 5f;
    private float time;
    public boolean display;

    public AccuracyMarker(float time) {
        this.time = time;
        display = true;
    }

    public void update(float delta) {
        if (!display)
            return;
        displayTime -= delta;
        if (displayTime <= 0) {
            display = false;
        }
    }

    public float getTime() {
        return time;
    }
}
