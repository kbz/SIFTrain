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
import com.fteams.siftrain.objects.AccuracyPopup;
import com.fteams.siftrain.objects.CircleMark;
import com.fteams.siftrain.objects.TapZone;
import com.fteams.siftrain.screens.ResultsScreen;
import com.fteams.siftrain.util.SongUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldController implements Music.OnCompletionListener {
    private World world;

    private final Array<CircleMark> marks;
    private final Array<TapZone> tapZones;
    // osu! style accuracy marker on top
    private final Array<AccuracyMarker> accuracyMarkers;
    // the text on screen "perfect", ..., "miss"
    private final Array<AccuracyPopup> accuracyPopups;

    public boolean done;
    private boolean hasMusic;

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

    Float aPosition;
    Float bPosition;

    float songStart;
    boolean songStarted;
    boolean isABRepeatMode;

    private Music theSong;
    private Integer syncMode;

    private boolean rewinding;

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
        this.accuracyPopups = world.getAccuracyPopups();
        this.leftMark = true;
        this.songStart = world.delay;
        this.songStarted = false;
        this.mtime = 0f;
        this.lastmtime = 0f;
        this.time = 0f;
        this.oldTime = 0f;
        this.timeSyncAcc = 0f;
        this.syncMode = GlobalConfiguration.syncMode;
        this.isABRepeatMode = GlobalConfiguration.playbackMode != null && GlobalConfiguration.playbackMode.equals(SongUtils.GAME_MODE_ABREPEAT);

        if (GlobalConfiguration.playbackRate == null || GlobalConfiguration.playbackRate.compareTo(1.0f) == 0) {
            theSong = SongLoader.loadSongFile();
        }
        if (isABRepeatMode) {
            aPosition = GlobalConfiguration.aTime;
            // set a buffer of 3 seconds previous to the fragment we're practicing
            aPosition = aPosition - 3f < 0.0f ? 0.0f : aPosition - 3f;
            bPosition = GlobalConfiguration.bTime;
            if (GlobalConfiguration.playbackRate != null && GlobalConfiguration.playbackRate.compareTo(1.0f) != 0)
                aPosition = aPosition / GlobalConfiguration.playbackRate;
            if (GlobalConfiguration.playbackRate != null && GlobalConfiguration.playbackRate.compareTo(1.0f) != 0)
                bPosition = bPosition / GlobalConfiguration.playbackRate;

            time = aPosition;
        }
        this.hasMusic = theSong != null;
        this.rewinding = false;
    }

    private void resetMarks() {
        this.rewinding = true;
        for (CircleMark circle : marks) {

            circle.reset();
        }
        this.combo = 0;
        this.badCount = 0;
        this.goodCount = 0;
        this.greatCount = 0;
        this.perfectCount = 0;
        this.missCount = 0;
        this.largestCombo = 0;
        this.timeSyncAcc = 0f;
        accuracyMarkers.clear();
        accuracyPopups.clear();
        this.rewinding = false;
    }

    @Override
    public void onCompletion(Music music) {
        if (isABRepeatMode && !done) {
            resetMarks();
            if (hasMusic) {
                theSong.pause();
                theSong.setPosition(aPosition);
                theSong.play();
                lastmtime = theSong.getPosition();
                time = lastmtime + world.delay;
                timeSyncAcc = 0;
            } else {
                time = aPosition;
            }
            return;
        }

        if (hasMusic) {
            music.dispose();
        }
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
        Results.accuracy = calculateAccuracy();
        Results.normalizedAccuracy = calculateNormalizedAccuracy();
        accuracyMarkers.clear();
        accuracyPopups.clear();
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

    public float mtime;
    public float time;
    public float lastmtime;
    public float oldTime;
    public float timeSyncAcc;


    public void update(float delta) {
        if (!world.started)
            return;

        if (world.paused)
            return;

        if (rewinding)
            return;

        // some song files may start immediately and the beatmaps may have notes which start
        // immediately with the songs, so give them a small lead-in to spawn the notes
        if (!songStarted) {
            songStart -= delta;
            if (songStart <= 0) {
                songStarted = true;
                if (hasMusic) {
                    theSong.setLooping(false);
                    theSong.setOnCompletionListener(this);
                    theSong.setVolume(GlobalConfiguration.songVolume / 100f);
                    theSong.play();
                    if (aPosition != null)
                        theSong.setPosition(aPosition);
                    lastmtime = theSong.getPosition();
                    time = lastmtime + world.delay;
                    timeSyncAcc = 0;
                } else {
                    if (aPosition != null) {
                        lastmtime = aPosition;
                        time = lastmtime + world.delay;
                        timeSyncAcc = 0;
                    }
                }
            }
        }
        // sync music and beatmap if there's music
        sync(delta);

        for (CircleMark mark : marks) {
            mark.update(time);
        }
        for (AccuracyMarker marker : world.getAccuracyMarkers()) {
            marker.update(delta);
        }
        for (AccuracyPopup popup : world.getAccuracyPopups()) {
            popup.update(delta);
        }
        processInput();
    }

    private void sync(float delta) {
        float theTime = time;
        if (hasMusic) {
            switch (syncMode) {
                case 0: {
                    mtime = theSong.getPosition();
                    if (mtime <= 0f && !songStarted) {
                        time += delta;
                        // use the first 300 ms of the song to sync
                    } else if (songStarted && mtime < 0.3f) {
                        time = mtime + world.delay;
                        lastmtime = mtime;
                        // if we haven't synced in a while
                    } else if (timeSyncAcc > 0.5f) {
                        lastmtime = mtime;
                        time = mtime + world.delay;
                        timeSyncAcc = 0f;
                        // if the time didn't update we interpolate the delta
                    } else if (lastmtime == mtime) {
                        time += delta;
                        timeSyncAcc += delta;
                        // if the new reading is behind the previous one, we interpolate the delta
                    } else if (mtime < lastmtime) {
                        time = lastmtime + world.delay + delta;
                        lastmtime = lastmtime + delta;
                        timeSyncAcc += delta;
                        // if the new reading is way ahead, we interpolate the delta
                    } else if (mtime > oldTime + 2 * delta) {
                        time = lastmtime + world.delay + delta;
                        lastmtime = lastmtime + delta;
                        timeSyncAcc += delta;
                    } else {
                        lastmtime = mtime;
                        time = mtime + world.delay;
                        timeSyncAcc = 0f;
                    }
                    // smoothen transitions if the new time is ahead or behind of the time + delta
                    float theDiff = time - (theTime + delta);
                    time = theTime + delta + theDiff * 1 / Gdx.graphics.getFramesPerSecond();
                    break;
                }
                case 1: {
                    mtime = theSong.getPosition();
                    if (mtime <= lastmtime) {
                        time += delta;
                    } else {
                        time = mtime + world.delay;
                    }
                    break;
                }
                case 2: {
                    mtime = theSong.getPosition();
                    if (timeSyncAcc < 0.5f) {
                        if (mtime <= lastmtime) {
                            time += delta;
                        } else {
                            time = mtime + world.delay;
                            lastmtime = mtime;
                        }
                    } else {
                        time += delta;
                    }
                    timeSyncAcc += delta;
                    break;
                }
                default:
                    time += delta;
                    break;
            }

        } else
        // otherwise just play the beatmap
        {
            time += delta;
        }
        oldTime = time;
    }

    private float calculateAccuracy() {
        float sum = 0f;
        List<Float> high = new ArrayList<>();
        List<Float> low = new ArrayList<>();
        for (AccuracyMarker hit : world.getAccuracyMarkers()) {
            sum += hit.getTime();
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
            world.started = true;
            if (hasMusic) {
                theSong.setLooping(false);
                theSong.setOnCompletionListener(this);
                theSong.setVolume(GlobalConfiguration.songVolume / 100f);
            }
        } else {
            if (world.paused) {
                world.paused = false;
                if (hasMusic) {
                    theSong.setPosition(lastmtime);
                    time = lastmtime + world.delay;
                    theSong.play();
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
            if (!mark.waiting) {
                continue;
            }
            if (mark.notePosition == (matchedId)) {
                CircleMark.Accuracy accuracy = mark.hit();
                // if we tap too early, ignore this tap
                if (accuracy == CircleMark.Accuracy.NONE)
                    continue;

                playSoundForAccuracy(accuracy);
                if (!mark.hold) {
                    processAccuracy(accuracy, null, false);
                    leftMark = !leftMark;
                }
                if (mark.hold && accuracy.compareTo(CircleMark.Accuracy.GOOD) <= 0) {
                    if (combo > largestCombo) {
                        largestCombo = combo;
                    }
                    combo = 0;
                    world.combo = 0;
                }
                accuracyPopups.add(new AccuracyPopup(accuracy, mark.accuracyHitStartTime < 0));
                accuracyMarkers.add(new AccuracyMarker(mark.accuracyHitStartTime));
                accuracyList.add(accuracy);
                // 1 mark per tap
                break;
            }

        }
    }

    private void release(int matchedId) {
        for (CircleMark mark : marks) {
            if (!mark.hold) {
                continue;
            }
            if (!mark.waiting) {
                continue;
            }

            if (matchedId == mark.notePosition) {
                CircleMark.Accuracy accuracy = mark.release();
                // releasing in the same zone as an upcoming hold can cause 'None' results
                if (accuracy == CircleMark.Accuracy.NONE)
                    continue;
                if (accuracy != CircleMark.Accuracy.MISS) {
                    playSoundForAccuracy(accuracy);
                    leftMark = !leftMark;
                    accuracyMarkers.add(new AccuracyMarker(mark.accuracyHitEndTime));
                }
                accuracyPopups.add(new AccuracyPopup(accuracy, accuracy != CircleMark.Accuracy.MISS && mark.accuracyHitEndTime < 0));
                processAccuracy(mark.accuracyStart, accuracy, true);
                accuracyList.add(accuracy);
                // 1 mark per release
                break;
            }
        }
    }


    private void processInput() {
        boolean done = true;
        for (CircleMark mark : marks) {
            if (done && !mark.isDone()) {
                done = false;
            }
            if (!mark.processed && mark.isDone()) {
                mark.processed = true;
                if (!mark.hold) {
                    if (mark.accuracyStart == CircleMark.Accuracy.MISS) {
                        accuracyPopups.add(new AccuracyPopup(CircleMark.Accuracy.MISS, false));
                        processAccuracy(mark.accuracyStart, null, false);
                        accuracyList.add(mark.accuracyStart);
                    }
                } else {
                    if (mark.accuracyStart == CircleMark.Accuracy.MISS) {
                        accuracyPopups.add(new AccuracyPopup(CircleMark.Accuracy.MISS, false));
                        processAccuracy(mark.accuracyStart, null, false);
                        accuracyList.add(mark.accuracyStart);

                    } else if (mark.accuracyEnd == CircleMark.Accuracy.MISS) {
                        processAccuracy(mark.accuracyEnd, null, false);
                        accuracyPopups.add(new AccuracyPopup(CircleMark.Accuracy.MISS, false));
                    }
                }
            }
        }
        if (isABRepeatMode) {
            if (time + world.delay >= bPosition) {
                resetMarks();
                if (hasMusic) {
                    theSong.pause();
                    theSong.setPosition(aPosition);
                    theSong.play();
                    lastmtime = theSong.getPosition();
                    time = lastmtime + world.delay;
                    timeSyncAcc = 0;
                }
            }
        }
        if (done && !hasMusic) {
            this.onCompletion(null);
        }
        if (time > world.getDuration() / (GlobalConfiguration.playbackRate == null ? 1.0f : GlobalConfiguration.playbackRate) + world.delay) {
            if (!hasMusic)
                this.onCompletion(null);
        }
    }

    public void back() {
        if (world.started) {
            // if the game was paused and we pressed back again, we skip to the results screen
            if (world.paused) {
                this.done = true;
                this.onCompletion(theSong);
                return;
            }
            world.paused = true;
            if (hasMusic) {
                theSong.pause();
                lastmtime = theSong.getPosition();
                time = lastmtime + world.delay;
                timeSyncAcc = 0;
            }
        }
    }

}
