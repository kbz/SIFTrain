package com.fteams.siftrain.controller;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.fteams.siftrain.assets.GlobalConfiguration;

public class Crossfader implements Disposable {
    private static final float fadeRate = 0.25f;

    private Music fadingIn = null;
    private Music fadingOut = null;
    private Music enqueued = null;
    private boolean enqueuedStop = false;
    private float volume;

    public Crossfader() {
        volume = GlobalConfiguration.songVolume / 100.0f;
    }

    private void disposeOf(Music mus) {
        if(mus == null)
            return;;

        if(mus.isPlaying())
            mus.stop();

        mus.dispose();
    }

    private void initMusic(Music mus) {
        mus.setLooping(true);
        mus.setVolume(0.0f);
        mus.play();
    }

    @Override
    public void dispose() {
        disposeOf(fadingIn);
        disposeOf(fadingOut);
        disposeOf(enqueued);
        fadingIn = null;
        fadingOut = null;
        enqueued = null;
    }

    private void fadeIn() {
        if(enqueued != null)
            initMusic(enqueued);

        disposeOf(fadingOut);
        fadingOut = fadingIn;
        fadingIn = enqueued;
        enqueued = null;
        enqueuedStop = false;
    }

    private void tryFadeIn() {
        if(enqueued != null || enqueuedStop)
            fadeIn();
    }

    public void enqueue(Music mus) {
        disposeOf(enqueued);
        enqueued = mus;
        enqueuedStop = (mus == null);

        if(fadingOut == null)
            fadeIn();
    }

    public void update(float delta) {
        final float volStep = volume * delta * fadeRate;

        if(fadingIn != null && !fadingIn.isPlaying()) {
            fadingIn.dispose();
            fadingIn = null;
        }

        if(fadingOut != null && !fadingOut.isPlaying()) {
            fadingOut.dispose();
            fadingOut = null;
            tryFadeIn();
        }

        if(fadingOut != null) {
            final float newVol = MathUtils.clamp(fadingOut.getVolume() - volStep, 0f, volume);
            if(newVol > 0f)
                fadingOut.setVolume(newVol);
            else {
                disposeOf(fadingOut);
                fadingOut = null;
                tryFadeIn();
            }
        }

        if(fadingIn != null && fadingIn.getVolume() < volume) {
            fadingIn.setVolume(MathUtils.clamp(fadingIn.getVolume() + volStep, 0f, volume));
        }
    }
}
