package com.fteams.siftrain.objects;

public class ScoreDiffMarker {
    public int value;
    public float displayTime;
    public boolean display;
    public boolean left;
    public ScoreDiffMarker(int score, float displayTime, boolean left)
    {
        this.left = left;
        this.value = score;
        this.displayTime = displayTime;
        this.display = true;
    }
    public void update(float delta)
    {
        // save cpu cycles
        if (!display)
            return;

        displayTime -= delta;
        if (displayTime <= 0)
        {
            display = false;
        }
    }
}
