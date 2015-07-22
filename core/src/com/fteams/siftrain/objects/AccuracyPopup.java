package com.fteams.siftrain.objects;

public class AccuracyPopup {
    private float screenTime;
    public boolean show;
    public CircleMark.Accuracy accuracy;
    public boolean soon;
    public boolean fadeIn;
    public float fadeTime = 0.25f;

    public AccuracyPopup(CircleMark.Accuracy accuracy, boolean soon) {
        this.screenTime = 0.5f;
        this.accuracy = accuracy;
        this.soon = soon;
        fadeIn = true;
        fadeTime = 0.25f;
        show = true;
    }

    public void update(float delta) {
        screenTime -= delta;
        fadeTime -= delta;
        if (screenTime <= 0 && show) {
            show = false;
        }
        if (fadeTime <= 0 && fadeIn) {
            fadeIn = false;
        }
    }

    public float getSize() {
        return 1f + (0.5f - screenTime);
    }

    public float getAlpha() {
        float alpha = fadeIn ? 0.75f + screenTime : screenTime / 0.25f;
        return (alpha >= 1f ? 1f : (alpha <= 0f ? 0f : alpha));
    }
}
