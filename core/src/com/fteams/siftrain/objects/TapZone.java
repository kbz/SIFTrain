package com.fteams.siftrain.objects;

import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;

public class TapZone {
    public TapZone(float x, float y, int i) {
        this.id = i;
        this.position.x = x;
        this.position.y = y;

        this.tapZoneDistance = (float) Math.sqrt(x * x + y * y);
        this.tapAngle = (float) Math.acos(x / this.tapZoneDistance);

        stateMap.put(State.STATE_PRESSED, false);
        stateMap.put(State.STATE_WARN, false);
    }

    private HashMap<State, Boolean> stateMap = new HashMap<>();

    public void update(float delta) {

    }

    public Integer getId() {
        return id;
    }

    public Boolean getState(State state) {
        return stateMap.get(state);
    }

    public synchronized void setState(State state, Boolean value) {
        stateMap.put(state, value);
    }

    public enum State {
        STATE_PRESSED, STATE_WARN
    }

    Vector2 position = new Vector2();
    Integer id;
    public float touchTime = -1f;

    float tapZoneDistance;
    float tapAngle;

    public Vector2 getPosition() {
        return position;
    }

    public float getTapZoneDistance() {
        return tapZoneDistance;
    }

    public float getTapAngle() {
        return tapAngle;
    }
}
