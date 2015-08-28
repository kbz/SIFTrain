package com.fteams.siftrain.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.fteams.siftrain.assets.Assets;
import com.fteams.siftrain.assets.GlobalConfiguration;
import com.fteams.siftrain.assets.Results;
import com.fteams.siftrain.entities.SimpleNotesInfo;
import com.fteams.siftrain.util.SongUtils;

public class CircleMark implements Comparable<CircleMark> {

    public float getSize() {
        return size;
    }

    public int getEffectMask() {
        return effect;
    }

    public SimpleNotesInfo getNote() {
        return note;
    }

    public Vector2 getHoldReleasePosition() {
        return holdReleasePosition;
    }

    @Override
    public int compareTo(CircleMark o) {
        if (this.spawnTime != o.spawnTime) {
            return Float.compare(spawnTime, o.spawnTime);
        }
        return Integer.compare(destination, o.destination);

    }

    public enum Accuracy {
        NONE, MISS, BAD, GOOD, GREAT, PERFECT

    }

    public boolean visible;
    public boolean waiting;
    public boolean holding;
    public boolean miss;
    public boolean endVisible;
    public boolean waitingStart;
    public boolean waitingEnd;
    public boolean processed;
    public boolean hold;
    public boolean soundPlayed;

    public boolean left;
    Vector2 origin = new Vector2();
    Vector2 position = new Vector2();
    Vector2 holdReleasePosition = new Vector2();
    Vector2 velocity = new Vector2();

    public Integer notePosition;
    public Integer destination = 0;
    public Double speed;
    SimpleNotesInfo note;

    private float spawnTime;
    private float despawnTime;
    private float startWaitTime;
    private float endWaitTime;
    private float holdEndSpawnTime;
    private float holdEndDespawnTime;
    private float holdEndStartWaitTime;
    private float holdEndEndWaitTime;

    public  float alpha = 1f;
    public Integer effect;
    // only for holds
    private float size;
    private float size2;


    public CircleMark(float x, float y, SimpleNotesInfo note, Double noteSpeed, float delay) {
        float timing = (float) (delay + note.timing_sec * 1f + GlobalConfiguration.offset * 1f / 1000f);
        notePosition = note.position;
        this.origin.x = x;
        this.origin.y = y;
        this.position.x = x;
        this.position.y = y;
        this.holdReleasePosition.x = x;
        this.holdReleasePosition.y = y;
        this.note = note;
        this.effect = note.effect;
        this.hold = (note.effect & SongUtils.NOTE_TYPE_HOLD) != 0;
        // position goes 9-8-...-2-1
        this.destination = note.position - 1;
        this.speed = noteSpeed;
        this.spawnTime = (float) (timing - speed);
        this.startWaitTime = (float) (timing - speed);
        this.endWaitTime = timing + (float) (0.5f * speed);
        this.despawnTime = timing * 1.0f;
        this.size = 0.1f;
        this.size2 = 0.1f;
        if (hold) {
            this.holdEndSpawnTime = (float) (timing + note.effect_value - speed);
            this.holdEndStartWaitTime = (float) (timing + note.effect_value - speed);
            this.holdEndEndWaitTime = (float) (timing + note.effect_value + 0.5f * speed);
            this.holdEndDespawnTime = (float) (timing + note.effect_value);

        }
        accuracyHitStartTime = -9f;
        accuracyHitEndTime = -9f;
        previousTime = 0f;

        initializeVelocity();
        initializeStates();
    }

    private boolean firstHit;
    private boolean secondHit;

    public Accuracy accuracyStart;
    public Accuracy accuracyEnd;

    public float accuracyHitStartTime;
    public float accuracyHitEndTime;

    public void updateDestination(int newDestination) {
        this.destination = newDestination;
        this.notePosition = newDestination + 1;
        // reset the velocity vectors;
        initializeVelocity();
    }


    private void initializeStates() {
        visible = false;
        waiting = false;
        miss = false;
        holding = false;
        endVisible = false;
        waitingStart = false;
        waitingEnd = false;
        processed = false;
        soundPlayed = false;
        alpha = 1f;
    }

    private void initializeVelocity() {
        float xVel = (float) Math.cos((destination) * Math.PI / 8);
        float yVel = -(float) Math.sin((destination) * Math.PI / 8);
        velocity.x = (float) (xVel * 249 / speed);
        velocity.y = (float) (yVel * 249 / speed);
    }

    public boolean isDone() {
        return miss || (firstHit && secondHit);
    }

    float previousTime;

    public void update(float time) {
        if (miss || (firstHit && secondHit)) {
            if (visible) {
                visible = false;
            }
            if (endVisible) {
                endVisible = false;
            }
            return;
        }
        updateFirst(time);
        if (hold) {
            updateSecond(time);
        }
        processMiss(time);
        previousTime = time;
    }


    private void updateFirst(float time) {

        if (spawnTime <= time && despawnTime > time && !visible) {
            visible = true;
        }

        if (spawnTime >= time && visible)
            visible = false;

        if (visible && despawnTime <= time) {
            if(GlobalConfiguration.playHintSounds && !soundPlayed) {
                Assets.perfectSound.play(GlobalConfiguration.feedbackVolume / 200f);
                soundPlayed = true;
            }

            if(holding) {
                alpha = 1f;
            } else {
                alpha = MathUtils.clamp((endWaitTime - time) / (endWaitTime - despawnTime), 0f, 1f);
                if(alpha == 0f)
                    visible = false;
            }
        }

        if (visible) {
            float scl = time - spawnTime;

            if(holding) {
                scl = speed.floatValue();
                updateSize(Math.max(0f, despawnTime - time));
            } else
                updateSize(despawnTime - time);

            position.set(origin.cpy().add(velocity.cpy().scl(scl)));
        }
        if (startWaitTime <= time && endWaitTime > time && !waiting) {
            waiting = true;
            waitingStart = true;
        }
    }

    private void updateSecond(float time) {

        if (holdEndSpawnTime <= time && holdEndDespawnTime > time && !endVisible) {
            endVisible = true;
            waitingEnd = true;
        }
        if (holdEndSpawnTime >= time || holdEndDespawnTime <= time) {
            if (endVisible) {
                if (GlobalConfiguration.playHintSounds) {
                    Assets.perfectSound.play(GlobalConfiguration.feedbackVolume / 200f);
                }
                endVisible = false;
            }
        }
        if (endVisible) {
            updateSize2(holdEndDespawnTime - time);
            holdReleasePosition.add(velocity.cpy().scl(time - previousTime));
        }

        if (holdEndStartWaitTime <= time && holdEndEndWaitTime > time && !waitingEnd && waiting) {
            waitingEnd = true;
        }
    }

    private void processMiss(float time) {
        // miss if we miss the first note
        if (hold && !firstHit && !holding && endWaitTime <= time && waitingStart && !miss) {
            waiting = false;
            endVisible = false;
            miss = true;
            accuracyStart = Accuracy.MISS;
            accuracyEnd = Accuracy.MISS;
            // System.out.println("MISS-001: didn't hit the note");
        } else if (!hold && endWaitTime <= time && waitingStart && !miss) {
            waiting = false;
            endVisible = false;
            miss = true;
            accuracyStart = Accuracy.MISS;
            //System.out.println("MISS-002: didn't hit the note");
        }
        if (hold && !miss) {
            // miss if we hold for too long
            if (holdEndEndWaitTime <= time && waitingEnd && !miss) {
                //System.out.println("MISS-003: held for too long");
                miss = true;
                waitingEnd = false;
                holding = false;
                endVisible = false;
                waiting = false;
                accuracyEnd = Accuracy.MISS;
            }
            // miss if we release before we start waiting
            if (firstHit && holdEndSpawnTime > time && !holding && !miss) {
                accuracyEnd = Accuracy.MISS;
                waiting = false;
                endVisible = false;
                miss = true;
                //System.out.println("MISS-004: released hold too early");
            }
        }
    }

    public float getSize2() {
        return this.size2;
    }

    public Accuracy hit() {
        Accuracy accuracy = Results.getAccuracyFor(previousTime - despawnTime - GlobalConfiguration.inputOffset / 1000f, speed);
        // If the note was tapped too early, we ignore the tap
        if (despawnTime > previousTime && accuracy == Accuracy.MISS) {
            return Accuracy.NONE;
        }
        accuracyHitStartTime = previousTime - despawnTime - GlobalConfiguration.inputOffset / 1000f;
        if (hold) {
            holding = true;
            accuracyStart = accuracy;
            firstHit = true;
        } else {
            accuracyStart = accuracy;
            accuracyEnd = Accuracy.NONE;
            firstHit = true;
            secondHit = true;
            processed = true;
            waiting = false;
            visible = false;
        }
        waitingStart = false;
        // calculate hit accuracy
        return accuracyStart;
    }


    public Accuracy release() {
        if (!firstHit) {
            return Accuracy.NONE;
        }
        secondHit = true;
        accuracyHitEndTime = previousTime - holdEndDespawnTime - GlobalConfiguration.inputOffset / 1000f;
        waitingEnd = false;
        holding = false;
        endVisible = false;
        waiting = false;
        accuracyEnd = Results.getAccuracyFor(previousTime - holdEndDespawnTime - GlobalConfiguration.inputOffset / 1000f, speed);
        if (accuracyEnd == Accuracy.MISS) {
            processed = true;
            //System.out.println("MISS-005: Released hold too early");
        }
        return accuracyEnd;
    }

    private void updateSize(float despawnTime) {
        float progress = (float) ((speed - despawnTime) / speed);
        this.size = 0.1f + progress * 0.9f;
    }

    private void updateSize2(float despawnTime) {
        float progress = (float) ((speed - despawnTime) / speed);
        this.size2 = 0.1f + progress * 0.9f;
    }

    public Vector2 getPosition() {
        return position;
    }
}
