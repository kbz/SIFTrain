package com.fteams.siftrain.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Array;
import com.fteams.siftrain.World;
import com.fteams.siftrain.assets.Assets;
import com.fteams.siftrain.assets.GlobalConfiguration;
import com.fteams.siftrain.assets.Results;
import com.fteams.siftrain.objects.AccuracyMarker;
import com.fteams.siftrain.objects.CircleMark;
import com.fteams.siftrain.objects.ScoreDiffMarker;
import com.fteams.siftrain.objects.TapZone;
import com.fteams.siftrain.screens.ResultsScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldController implements Music.OnCompletionListener {
    private World world;

    private final Array<CircleMark> marks;
    private final Array<TapZone> tapZones;
    private final Array<AccuracyMarker> accuracyMarkers;
    private final Array<ScoreDiffMarker> scoreMarkers;

    public boolean done;

    public int combo;

    private int badCount;
    private int goodCount;
    private int greatCount;
    private int perfectCount;
    private int missCount;

    private int largestCombo;
    private List<CircleMark.Accuracy> accuracyList;

    Map<Integer, Integer> pointerToZoneId = new HashMap<>();
    private boolean leftMark;


    public WorldController(World world) {
        this.world = world;
        this.marks = world.getMarks();
        this.tapZones = world.getZones();
        this.accuracyMarkers = world.getAccuracyMarkers();
        this.combo = 0;
        this.badCount = 0;
        this.goodCount = 0;
        this.greatCount = 0;
        this.perfectCount = 0;
        this.missCount = 0;
        this.largestCombo = 0;
        this.accuracyList = new ArrayList<>();
        this.scoreMarkers = world.getScoreMarkers();
        this.acted = false;
        this.leftMark = true;
    }

    @Override
    public void onCompletion(Music music) {
        done = true;
        if (this.largestCombo < this.combo) {
            this.largestCombo = combo;
        }
        Results.bads = badCount;
        Results.goods = goodCount;
        Results.greats = greatCount;
        Results.perfects = perfectCount;
        Results.miss = missCount;

        Results.combo = largestCombo;
        Results.score = world.score;
        Results.accuracy = calculateAccuracy();
        Results.normalizedAccuracy = calculateNormalizedAccuracy();
        if (music != null) {
            music.dispose();
        }
        accuracyMarkers.clear();
        marks.clear();
        tapZones.clear();
        ((Game) Gdx.app.getApplicationListener()).setScreen(new ResultsScreen());
    }

    private int countType(Array<CircleMark> marks, Integer effect) {
        int sum = 0;
        for (CircleMark mark : marks) {
            if ((mark.effect & effect) != 0) {
                sum++;
            }
        }
        return sum;
    }

    private float calculateNormalizedAccuracy() {
        float sum = 0f;
        for (CircleMark.Accuracy accuracy : accuracyList) {
            sum += Results.getAccuracyMultiplierForAccuracy(accuracy);
        }
        return sum / accuracyList.size();
    }

    public void update(float delta) {
        if (!world.started)
            return;
        if (world.paused)
            return;
        for (CircleMark mark : marks) {
            mark.update(delta);
        }
        for (TapZone tapZone : tapZones) {
            tapZone.update(delta);
        }
        for (AccuracyMarker marker : world.getAccuracyMarkers()) {
            marker.update(delta);
        }
        for (ScoreDiffMarker marker : world.getScoreMarkers()) {
            marker.update(delta);
        }
        processInput();
    }

    private float calculateAccuracy() {
        float sum = 0f;
        List<Float> high = new ArrayList<>();
        List<Float> low = new ArrayList<>();
        for (AccuracyMarker hit : world.getAccuracyMarkers()) {
            sum += (hit.getTime());
        }
        float average = sum / world.getAccuracyMarkers().size;
        for (AccuracyMarker value : world.getAccuracyMarkers()) {
            if (value.getTime() >= average) {
                high.add(value.getTime());
            } else {
                low.add(value.getTime());
            }
        }

        Results.minAccuracy = calcAverage(low);
        Results.maxAccuracy = calcAverage(high);
        Results.unstableRating = 10 * calcDeviation(world.getAccuracyMarkers());

        return sum / world.getAccuracyMarkers().size;
    }

    private float calcAverage(List<Float> values) {
        float sum = 0;
        for (Float value : values) {
            sum += value;
        }
        return sum / values.size();
    }

    private float calcDeviation(Array<AccuracyMarker> values) {
        if (values.size == 0)
            return 0f;

        float sum = 0f;
        for (AccuracyMarker value : values) {
            sum += value.getTime();
        }

        float mean = sum / values.size;
        sum = 0f;
        for (AccuracyMarker value : values) {
            sum += (value.getTime() - mean) * (value.getTime() - mean);
        }
        return (float) Math.sqrt(sum / (values.size - 1));
    }

    public boolean acted;

    private void processAccuracy(CircleMark.Accuracy accuracy, CircleMark.Accuracy accuracy2, boolean isHold) {
        if (!isHold) {
            if (accuracy == CircleMark.Accuracy.BAD) {
                badCount++;
                if (combo > largestCombo) {
                    largestCombo = combo;
                }
                combo = 0;
                world.combo = 0;
            } else if (accuracy == CircleMark.Accuracy.GOOD) {
                goodCount++;
                if (combo > largestCombo) {
                    largestCombo = combo;
                }
                combo = 0;
                world.combo = 0;
            } else if (accuracy == CircleMark.Accuracy.GREAT) {
                greatCount++;
                combo++;
                world.combo = combo;
            } else if (accuracy == CircleMark.Accuracy.PERFECT) {
                perfectCount++;
                combo++;
                world.combo = combo;
            } else {
                missCount++;
                if (combo > largestCombo) {
                    largestCombo = combo;
                }
                combo = 0;
                world.combo = 0;
            }
        } else {
            // no combo break
            CircleMark.Accuracy lowest = accuracy.compareTo(accuracy2) >= 0 ? accuracy2 : accuracy;

            if (lowest == CircleMark.Accuracy.BAD) {
                badCount++;
            } else if (lowest == CircleMark.Accuracy.GOOD) {
                goodCount++;
            } else if (lowest == CircleMark.Accuracy.GREAT) {
                greatCount++;
            } else if (lowest == CircleMark.Accuracy.PERFECT) {
                perfectCount++;
            } else {
                missCount++;
            }
            if (accuracy2.compareTo(CircleMark.Accuracy.GOOD) > 0) {
                combo++;
                world.combo = combo;
            } else {
                if (combo > largestCombo) {
                    largestCombo = combo;
                }
                combo = 0;
                world.combo = 0;

            }

        }
    }

    private int calculateScore(CircleMark.Accuracy accuracy1, CircleMark.Accuracy accuracy2, boolean hold) {

        float accuracyMultiplier = Results.getMultiplierForAccuracy(accuracy1);
        if (hold) {
            accuracyMultiplier *= Results.getMultiplierForAccuracy(accuracy2);
        }
        float comboMultiplier = Results.getMultiplierForCombo(combo);
        float noteTypeMultiplier;
        float memberAttributeMultiplier;

        if (hold) {
            noteTypeMultiplier = 1.25f;
        } else {
            noteTypeMultiplier = 1.0f;
        }
        memberAttributeMultiplier = 1.10f;

        return (int) Math.floor(GlobalConfiguration.teamStrength * 0.0125 * accuracyMultiplier * comboMultiplier * noteTypeMultiplier * memberAttributeMultiplier);
    }

    private void playSoundForAccuracy(CircleMark.Accuracy accuracy) {
        if (accuracy == CircleMark.Accuracy.PERFECT) {

            Assets.perfectSound.play(GlobalConfiguration.feedbackVolume / 100f);
        }
        if (accuracy == CircleMark.Accuracy.GREAT) {
            Assets.greatSound.play(GlobalConfiguration.feedbackVolume / 100f);
        }
        if (accuracy == CircleMark.Accuracy.GOOD) {
            Assets.goodSound.play(GlobalConfiguration.feedbackVolume / 100f);
        }
        if (accuracy == CircleMark.Accuracy.BAD) {
            Assets.badSound.play(GlobalConfiguration.feedbackVolume / 100f);
        }
    }

    public void pressed(int screenX, int screenY, int pointer, int button, float ppuX, float ppuY, int width, int height) {
        playMusicOnDemand();

        int matchedId = getTapZoneForCoordinates(screenX, screenY, ppuX, ppuY, width, height, pointer);

        if (matchedId == -1) {
            return;
        }

        hit(matchedId);
    }

    public void released(int screenX, int screenY, int pointer, int button, float ppuX, float ppuY, int width, int height) {
        int matchedId = -1;
        for (TapZone zone : tapZones) {
            if (zone.getId().equals(pointerToZoneId.get(pointer))) {
                matchedId = zone.getId();
                pointerToZoneId.remove(pointer);
                zone.setState(TapZone.State.STATE_PRESSED, false);
            }
        }

        if (matchedId == -1) {
            return;
        }
        release(matchedId);

    }

    private void playMusicOnDemand() {
        if (!world.started) {
            if (Assets.music != null) {
                Assets.music.setLooping(false);
                Assets.music.setOnCompletionListener(this);
                Assets.music.setVolume(GlobalConfiguration.songVolume / 100f);
                Assets.music.play();
            }
            world.started = true;
        } else {
            if (world.paused) {
                world.paused = false;
                if (Assets.music != null) {
                    Assets.music.play();
                }
            }
        }
    }

    private int getTapZoneForCoordinates(int screenX, int screenY, float ppuX, float ppuY, int width, int height, int pointer) {
        float centerX = world.offsetX + width / 2;
        float centerY = world.offsetY + height * 0.25f;

        float relativeX = (screenX - centerX) / ppuX;
        float relativeY = (-screenY + centerY) / ppuY;

        float circleRadius = 400 * 0.1f;
        float relativeDistance = (float) Math.sqrt(relativeX * relativeX + relativeY * relativeY);
        float relativeAngle = (float) Math.acos(relativeX / relativeDistance);

        int matchedId = -1;
        for (TapZone zone : tapZones) {
            float x = zone.getPosition().x;
            float y = zone.getPosition().y;
            float tapZoneDistance = (float) Math.sqrt(x * x + y * y);
            if (tapZoneDistance - circleRadius * 2 < relativeDistance && relativeDistance < tapZoneDistance + circleRadius * 2) {
                float tapAngle = (float) Math.acos(x / tapZoneDistance);
                if (tapAngle - Math.PI / 16 < relativeAngle && relativeAngle < tapAngle + Math.PI / 16 && relativeY < circleRadius) {
                    matchedId = zone.getId();
                    zone.setState(TapZone.State.STATE_PRESSED, true);
                    pointerToZoneId.put(pointer, matchedId);
                }
            }
        }
        return matchedId;
    }

    private void hit(int matchedId) {
        for (CircleMark mark : marks) {
            if (!mark.getState(CircleMark.State.WAITING)) {
                continue;
            }
            if (mark.getNote().position == (matchedId)) {
                CircleMark.Accuracy accuracy = mark.hit();
                // if we tap too early, ignore this tap
                if (accuracy == CircleMark.Accuracy.NONE)
                    continue;

                playSoundForAccuracy(accuracy);
                world.accuracy = accuracy;
                if (!mark.isHold()) {
                    processAccuracy(accuracy, null, false);
                    int score = calculateScore(accuracy, null, false);
                    scoreMarkers.add(new ScoreDiffMarker(score, (float) (mark.speed * 0.4f), leftMark));
                    leftMark = !leftMark;
                    world.score += score;
                }
                if (mark.isHold() && accuracy.compareTo(CircleMark.Accuracy.GOOD) <= 0) {
                    if (combo > largestCombo) {
                        largestCombo = combo;
                    }
                    combo = 0;
                    world.combo = 0;
                }
                accuracyMarkers.add(new AccuracyMarker(mark.accuracyHitStartTime));
                accuracyList.add(accuracy);
                // 1 mark per tap
                break;
            }

        }
    }

    private void release(int matchedId) {
        for (CircleMark mark : marks) {
            if (!mark.isHold()) {
                continue;
            }
            if (!mark.getState(CircleMark.State.WAITING)) {
                continue;
            }

            if (matchedId == mark.getNote().position) {
                if (mark.isHold()) {
                    CircleMark.Accuracy accuracy = mark.release();
                    // releasing in the same zone as an upcoming hold can cause 'None' results
                    if (accuracy == CircleMark.Accuracy.NONE)
                        continue;
                    if (accuracy != CircleMark.Accuracy.MISS) {
                        playSoundForAccuracy(accuracy);
                        int score = calculateScore(mark.accuracyStart, mark.accuracyEnd, true);
                        scoreMarkers.add(new ScoreDiffMarker(score, (float) (mark.speed * 0.4f), leftMark));
                        leftMark = !leftMark;
                        world.score += score;
                        accuracyMarkers.add(new AccuracyMarker(mark.accuracyHitEndTime));
                    }
                    processAccuracy(mark.accuracyStart, accuracy, true);
                    accuracyList.add(accuracy);
                    world.accuracy = accuracy;
                    // 1 mark per release
                    break;
                }
            }
        }
    }


    private void processInput() {
        boolean done = true;
        for (CircleMark mark : marks) {
            if (!mark.isDone()) {
                done = false;
            }
            if (!mark.getState(CircleMark.State.PROCESSED) && mark.isDone()) {
                mark.setState(CircleMark.State.PROCESSED, true);
                if (!mark.isHold()) {
                    if (mark.accuracyStart == CircleMark.Accuracy.MISS) {
                        processAccuracy(mark.accuracyStart, null, false);
                        accuracyList.add(mark.accuracyStart);
                        world.accuracy = CircleMark.Accuracy.MISS;
                    }
                } else {
                    if (mark.accuracyStart == CircleMark.Accuracy.MISS) {
                        processAccuracy(mark.accuracyStart, null, false);
                        world.accuracy = CircleMark.Accuracy.MISS;
                        accuracyList.add(mark.accuracyStart);

                    } else if (mark.accuracyEnd == CircleMark.Accuracy.MISS) {
                        processAccuracy(mark.accuracyEnd, null, false);
                        world.accuracy = CircleMark.Accuracy.MISS;


                    }
                }
            }
        }
        if (done && Assets.music == null) {
            this.onCompletion(null);
        }
    }

    public void back() {
        if (world.started) {
            // if the game was paused and we pressed back again, we skip to the results screen
            if (world.paused) {
                this.onCompletion(Assets.music);
                return;
            }
            world.paused = true;
            if (Assets.music != null) {
                Assets.music.pause();
            }
        }
    }

}
