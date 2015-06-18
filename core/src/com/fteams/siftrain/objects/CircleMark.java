package com.fteams.siftrain.objects;

import com.badlogic.gdx.math.Vector2;
import com.fteams.siftrain.assets.Results;
import com.fteams.siftrain.entities.SimpleNotesInfo;
import com.fteams.siftrain.util.SongUtils;

import java.util.HashMap;
import java.util.Map;

public class CircleMark {

    public boolean getState(State state) {
        return stateMap.get(state);
    }

    public void setState(State state, boolean value) {
        stateMap.put(state, value);
    }


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

    public enum Accuracy {
        NONE, MISS, BAD, GOOD, GREAT, PERFECT

    }

    public enum State {
        VISIBLE, WAITING, HOLDING, MISS, END_VISIBLE, WAITING_START, WAITING_END, PROCESSED
    }

    Vector2 position = new Vector2();
    Vector2 holdReleasePosition = new Vector2();
    Vector2 velocity = new Vector2();
    public Vector2 hookPoint = new Vector2();
    public Vector2 hookPoint2 = new Vector2();

    public Integer destination = 0;
    Double speed;
    SimpleNotesInfo note;

    private Map<State, Boolean> stateMap = new HashMap<>();

    private float spawnTime;
    private float despawnTime;
    private float startWaitTime;
    private float endWaitTime;
    private float holdEndSpawnTime;
    private float holdEndDespawnTime;
    private float holdEndStartWaitTime;
    private float holdEndEndWaitTime;

    public Integer effect;
    // only for holds
    private float size;
    private float size2;

    public CircleMark(float x, float y, SimpleNotesInfo note, Double noteSpeed) {
        this.position.x = x;
        this.position.y = y;
        this.holdReleasePosition.x = x;
        this.holdReleasePosition.y = y;
        this.note = note;
        this.effect = note.effect;
        // position goes 9-8-...-2-1
        this.destination = note.position - 1;
        this.speed = noteSpeed;
        this.spawnTime = (float) (note.timing_sec - speed);
        this.startWaitTime = (float) (note.timing_sec - speed);
        this.endWaitTime = (float) (note.timing_sec + 0.5f);
        this.despawnTime = (float) (note.timing_sec * 1.0f);
        this.size = 0.1f;
        this.size2 = 0.1f;
        if (isHold()) {
            this.holdEndSpawnTime = (float) (note.timing_sec + note.effect_value - speed);
            this.holdEndStartWaitTime = (float) (note.timing_sec + note.effect_value - speed);
            this.holdEndEndWaitTime = (float) (note.timing_sec + note.effect_value + 0.5f);
            this.holdEndDespawnTime = (float) (note.timing_sec + note.effect_value);
            calculateHook();

        }
        initializeVelocity();
        initializeStates();
    }

    private void calculateHook() {
        hookPoint.x = 0;
        hookPoint.y = 0;
        hookPoint2.x = 0;
        hookPoint2.y = 0;
    }

    private boolean firstHit;
    private boolean secondHit;

    public Accuracy accuracyStart;
    public Accuracy accuracyEnd;

    public float accuracyHitStartTime;
    public float accuracyHitEndTime;

    private void initializeStates() {
        setState(State.VISIBLE, false);
        setState(State.WAITING, false);
        setState(State.MISS, false);
        setState(State.HOLDING, false);
        setState(State.END_VISIBLE, false);
        setState(State.WAITING_START, false);
        setState(State.WAITING_END, false);
        setState(State.PROCESSED, false);
    }

    private void initializeVelocity() {
        float xVel = (float) Math.cos((destination) * Math.PI / 8);
        float yVel = -(float) Math.sin((destination) * Math.PI / 8);
        velocity.x = (float) (xVel * 249 / speed);
        velocity.y = (float) (yVel * 249 / speed);
    }

    public boolean isDone() {
        return getState(State.MISS) || (firstHit && secondHit);
    }

    public void update(float delta) {
        if (getState(State.MISS) || (firstHit && secondHit)) {
            if (getState(State.VISIBLE)) {
                setState(State.VISIBLE, false);
            }
            if (getState(State.END_VISIBLE)) {
                setState(State.END_VISIBLE, false);
            }
            return;
        }
        spawnTime -= delta;
        despawnTime -= delta;
        startWaitTime -= delta;
        endWaitTime -= delta;

        if (spawnTime <= 0 && despawnTime > 0 && !getState(State.VISIBLE)) {
            setState(State.VISIBLE, true);
        }
        if (spawnTime >= 0 || despawnTime <= 0) {
            if (getState(State.VISIBLE)) {
                setState(State.VISIBLE, false);
            }
        }

        if (getState(State.VISIBLE)) {
            updateSize(despawnTime);
            position.add(velocity.cpy().scl(delta));
            hookPoint.add(velocity.cpy().scl(delta));
        }
        if (startWaitTime <= 0 && endWaitTime > 0 && !getState(State.WAITING)) {
            setState(State.WAITING, true);
            setState(State.WAITING_START, true);
        }
        // miss if we miss the first note
        if (isHold() && !firstHit && !getState(State.HOLDING) && endWaitTime <= 0 && getState(State.WAITING_START) && !getState(State.MISS)) {
            setState(State.WAITING, false);
            setState(State.END_VISIBLE, false);
            setState(State.VISIBLE, false);
            setState(State.MISS, true);
            accuracyStart = Accuracy.MISS;
            accuracyEnd = Accuracy.MISS;
            // System.out.println("MISS-001: didn't hit the note");
        } else if (!isHold() && endWaitTime <= 0 && getState(State.WAITING_START) && !getState(State.MISS)) {
            setState(State.WAITING, false);
            setState(State.END_VISIBLE, false);
            setState(State.VISIBLE, false);
            setState(State.MISS, true);
            accuracyStart = Accuracy.MISS;
            //System.out.println("MISS-002: didn't hit the note");
        }
        if (isHold() && !getState(State.MISS)) {
            holdEndSpawnTime -= delta;
            holdEndDespawnTime -= delta;
            holdEndStartWaitTime -= delta;
            holdEndEndWaitTime -= delta;

            if (holdEndSpawnTime <= 0 && holdEndDespawnTime > 0 && !getState(State.END_VISIBLE)) {
                setState(State.END_VISIBLE, true);
                setState(State.WAITING_END, true);
            }
            if (holdEndSpawnTime >= 0 || holdEndDespawnTime <= 0) {
                if (getState(State.END_VISIBLE)) {
                    setState(State.END_VISIBLE, false);
                }
            }
            if (getState(State.END_VISIBLE)) {
                updateSize2(holdEndDespawnTime);
                holdReleasePosition.add(velocity.cpy().scl(delta));
                hookPoint2.add(velocity.cpy().scl(delta));
            }

            if (holdEndStartWaitTime <= 0 && holdEndEndWaitTime > 0 && !getState(State.WAITING_END) && getState(State.WAITING)) {
                setState(State.WAITING_END, true);
            }
            // miss if we hold for too long
            if (holdEndEndWaitTime <= 0 && getState(State.WAITING_END) && !getState(State.MISS)) {
                //System.out.println("MISS-003: held for too long");
                //System.out.println(stateMap);
                setState(State.MISS, true);
                setState(State.WAITING_END, false);
                setState(State.HOLDING, false);
                setState(State.VISIBLE, false);
                setState(State.END_VISIBLE, false);
                setState(State.WAITING, false);
                accuracyEnd = Accuracy.MISS;
            }
            // miss if we release before we start waiting
            if (firstHit && holdEndSpawnTime > 0 && !getState(State.HOLDING) && !getState(State.MISS)) {
                accuracyEnd = Accuracy.MISS;
                setState(State.WAITING, false);
                setState(State.END_VISIBLE, false);
                setState(State.VISIBLE, false);
                setState(State.MISS, true);
                //System.out.println("MISS-004: released hold too early");
            }
        }
    }

    public float getSize2() {
        return this.size2;
    }

    public Accuracy hit() {
        accuracyHitStartTime = despawnTime;
        if (isHold()) {
            setState(State.HOLDING, true);
            accuracyStart = Results.getAccuracyFor(despawnTime, speed);
            firstHit = true;
        } else {
            accuracyStart = Results.getAccuracyFor(despawnTime, speed);
            accuracyEnd = Accuracy.NONE;
            firstHit = true;
            secondHit = true;
            setState(State.PROCESSED, false);
            setState(State.WAITING, false);
        }
        setState(State.WAITING_START, false);
        setState(State.VISIBLE, false);

        // calculate hit accuracy
        return accuracyStart;
    }


    public Accuracy release() {
        if (!firstHit) {
            return Accuracy.NONE;
        }
        secondHit = true;
        accuracyHitEndTime = holdEndDespawnTime;
        setState(State.WAITING_END, false);
        setState(State.HOLDING, false);
        setState(State.END_VISIBLE, false);
        setState(State.WAITING, false);
        accuracyEnd = Results.getAccuracyFor(holdEndDespawnTime, speed);
        if (accuracyEnd == Accuracy.MISS) {
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

    public Boolean isHold() {
        return (note.effect & SongUtils.NOTE_TYPE_HOLD) != 0;
    }
}
