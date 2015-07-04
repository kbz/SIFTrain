package com.fteams.siftrain;

import com.badlogic.gdx.utils.Array;
import com.fteams.siftrain.assets.Assets;
import com.fteams.siftrain.assets.GlobalConfiguration;
import com.fteams.siftrain.entities.SimpleNotesInfo;
import com.fteams.siftrain.entities.SimpleRankInfo;
import com.fteams.siftrain.objects.AccuracyMarker;
import com.fteams.siftrain.objects.CircleMark;
import com.fteams.siftrain.objects.ScoreDiffMarker;
import com.fteams.siftrain.objects.TapZone;
import com.fteams.siftrain.util.Randomizer;
import com.fteams.siftrain.util.SongUtils;

public class World {

    int width;
    int height;
    public int score;
    public int cScore;
    public int bScore;
    public int aScore;
    public int sScore;
    /**
     * The 9 zones the user can tap
     */
    Array<TapZone> zones = new Array<>();
    /**
     * The notes which spawn from the center
     */
    Array<CircleMark> marks = new Array<>();

    public int combo;
    public CircleMark.Accuracy accuracy;
    public boolean started;
    public int offsetX;
    public int offsetY;

    private Array<AccuracyMarker> accuracyMarkers;
    private Array<ScoreDiffMarker> scoreMarkers;

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
        Double noteSpeed = Assets.selectedSong.song_info.get(0).notes_speed;
        score = 0;
        if (Assets.selectedSong.rank_info != null && !Assets.selectedSong.rank_info.isEmpty() && Assets.selectedSong.rank_info.size() >= 4) {
            // rank array is sorted in ascending order, the first element is most likely a 0
            int shift = Assets.selectedSong.rank_info.get(0).rank_max == 0 ? 1 : 0;
            cScore = Assets.selectedSong.rank_info.get(shift).rank_max;
            bScore = Assets.selectedSong.rank_info.get(shift + 1).rank_max;
            aScore = Assets.selectedSong.rank_info.get(shift + 2).rank_max;
            sScore = Assets.selectedSong.rank_info.get(shift + 3).rank_max;

        } else {
            // ignore and set default values.
            cScore = SongUtils.getCScoreForSong(Assets.selectedSong.song_info.get(0).notes.size(), Assets.selectedSong.difficulty);
            bScore = SongUtils.getBScoreForSong(Assets.selectedSong.song_info.get(0).notes.size(), Assets.selectedSong.difficulty);
            aScore = SongUtils.getAScoreForSong(Assets.selectedSong.song_info.get(0).notes.size(), Assets.selectedSong.difficulty);
            sScore = SongUtils.getSScoreForSong(Assets.selectedSong.song_info.get(0).notes.size(), Assets.selectedSong.difficulty);
            Assets.selectedSong.rank_info.clear();
            Assets.selectedSong.rank_info.add(new SimpleRankInfo(cScore));
            Assets.selectedSong.rank_info.add(new SimpleRankInfo(bScore));
            Assets.selectedSong.rank_info.add(new SimpleRankInfo(aScore));
            Assets.selectedSong.rank_info.add(new SimpleRankInfo(sScore));
            Assets.selectedSong.rank_info.add(new SimpleRankInfo(0));
        }
        Double firstNote = Assets.selectedSong.song_info.get(0).notes.get(0).timing_sec;
        delay = 0f;
        if (firstNote < noteSpeed) {
            delay = (float) (noteSpeed - firstNote) + 0.5f;
        }
        for (SimpleNotesInfo notesInfo : Assets.selectedSong.song_info.get(0).notes) {
            CircleMark mark = new CircleMark(x, y, notesInfo, noteSpeed, delay);
            marks.add(mark);
        }
        marks.sort();

        if (GlobalConfiguration.random) {
            Randomizer randomizer = new Randomizer();
            randomizer.randomize(marks);
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
        accuracy = CircleMark.Accuracy.NONE;
        this.accuracyMarkers = new Array<>();
        this.scoreMarkers = new Array<>();
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

    public Array<ScoreDiffMarker> getScoreMarkers() {
        return scoreMarkers;
    }
}
