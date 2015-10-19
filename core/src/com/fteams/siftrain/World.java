package com.fteams.siftrain;

import com.badlogic.gdx.utils.Array;
import com.fteams.siftrain.assets.Assets;
import com.fteams.siftrain.assets.GlobalConfiguration;
import com.fteams.siftrain.entities.SimpleNotesInfo;
import com.fteams.siftrain.objects.AccuracyMarker;
import com.fteams.siftrain.objects.AccuracyPopup;
import com.fteams.siftrain.objects.CircleMark;
import com.fteams.siftrain.objects.TapZone;
import com.fteams.siftrain.util.random.ExtremeRandomizer;
import com.fteams.siftrain.util.random.KeepSidesRandomizer;
import com.fteams.siftrain.util.random.MirroredKeepSidesRandomizer;
import com.fteams.siftrain.util.random.Randomizer;
import com.fteams.siftrain.util.random.NewAlgorithmRandomizer;
import com.fteams.siftrain.util.random.OldAlgorithmRandomizer;
import com.fteams.siftrain.util.SongUtils;
import com.fteams.siftrain.util.random.SimpleRandomizer;

public class World {

    int width;
    int height;
    /**
     * The 9 zones the user can tap
     */
    Array<TapZone> zones = new Array<>();
    /**
     * The notes which spawn from the center
     */
    Array<CircleMark> marks = new Array<>();

    public int combo;
    public boolean started;
    public int offsetX;
    public int offsetY;

    private Array<AccuracyMarker> accuracyMarkers;
    private Array<AccuracyPopup> accuracyPopups;

    public boolean paused;

    // Getters -----------
    public Array<TapZone> getZones() {
        return zones;
    }

    // --------------------

    public World() {
        createWorld();
    }

    public float delay;

    private void createWorld() {
        float x = 0f;
        float y = 0f;
        Double noteSpeed = SongUtils.getDefaultNoteSpeedForApproachRate(GlobalConfiguration.noteSpeed);

        delay = Assets.selectedSong.lead_in != null ? Assets.selectedSong.lead_in : 0f;

        for (SimpleNotesInfo notesInfo : Assets.selectedSong.song_info.get(0).notes) {
            CircleMark mark = new CircleMark(x, y, notesInfo, noteSpeed, delay);
            marks.add(mark);
        }
        marks.sort();

        if (GlobalConfiguration.random) {
            switch (GlobalConfiguration.randomMode) {
                case 0: {
                    Randomizer oldAlgorithmRandomizer = new OldAlgorithmRandomizer();
                    oldAlgorithmRandomizer.randomize(marks);
                    break;
                }
                case 1: {
                    Randomizer newAlgorithmRandomizer = new NewAlgorithmRandomizer();
                    newAlgorithmRandomizer.randomize(marks);
                    break;
                }
                case 2: {
                    Randomizer keepSidesRandomizer = new KeepSidesRandomizer();
                    keepSidesRandomizer.randomize(marks);
                    break;
                }
                case 3: {
                    Randomizer mirroredKeepSidesRandomizer = new MirroredKeepSidesRandomizer();
                    mirroredKeepSidesRandomizer.randomize(marks);
                    break;
                }
                case 4: {
                    Randomizer simpleRandomizer = new SimpleRandomizer();
                    simpleRandomizer.randomize(marks);
                    break;
                }
                case 5:{
                    Randomizer extremeRandomizer = new ExtremeRandomizer();
                    extremeRandomizer.randomize(marks);
                    break;
                }
                default:
                    break;
            }
        }

        float step = (float) (Math.PI / 8);
        float distance = 600f / 2 - 400f * 0.1275f;

        for (int i = 0; i < 9; i++) {
            float angle = step * i;
            x = (float) (distance * Math.cos(angle));
            y = -(float) (distance * Math.sin(angle));
            TapZone zone = new TapZone(x, y, i + 1);
            zones.add(zone);
        }
        this.accuracyMarkers = new Array<>();
        this.accuracyPopups = new Array<>();
        paused = false;
    }

    public void setSize(int width, int height, int offsetX, int offsetY) {
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public Array<CircleMark> getMarks() {
        return marks;
    }

    public Array<AccuracyMarker> getAccuracyMarkers() {
        return accuracyMarkers;
    }

    public Array<AccuracyPopup> getAccuracyPopups() {
        return accuracyPopups;
    }
}
