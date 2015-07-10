package com.fteams.siftrain.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.fteams.siftrain.entities.SimpleNotesInfo;
import com.fteams.siftrain.entities.SimpleRankInfo;
import com.fteams.siftrain.entities.SimpleSong;
import com.fteams.siftrain.entities.SongFileInfo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleSongLoader {
    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    public List<String> getErrors() {
        return errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public SimpleSong loadSong(SongFileInfo beatmap) {
        FileHandle handle = Gdx.files.absolute(Gdx.files.getExternalStoragePath() + "/" + beatmap.getFileName());
        SimpleSong song = null;
        String jsonDefinition = handle.readString("UTF-8");
        try {
            song = new Gson().fromJson(jsonDefinition, SimpleSong.class);
            validateSong(song);
            song.setValid(true);
            beatmap.song_name = song.song_name;
            if (errors.size() > 0) {
                beatmap.song_name = "Error: Beatmap format invalid. (" + handle.file().getName() + ")";
                song.setValid(false);
            }
        } catch (Exception e) {
            song = new SimpleSong();
            song.song_name = "Invalid JSON Format: " + handle.file().getName();
            song.difficulty = 1;
            beatmap.song_name = "Error: Invalid JSON Format. (" + handle.file().getName() + ")";
            errors.add("Invalid JSON Format");
            song.setValid(false);
        } finally {
            if (song != null) {
                song.setResourceName(handle.nameWithoutExtension().replaceAll("_(easy|normal|hard|expert)$", ""));
            }
        }
        return song;
    }

    public void validateSong(SimpleSong song) {
        if (song.difficulty == null) {
            errors.add("song_info: difficulty not specified.");
            return;
        }
        validateRanks(song);
        validateSongInfo(song);
    }

    public boolean validateRanks(SimpleSong song) {
        List<String> errors = new ArrayList<>();
        if (song.rank_info == null) {
            warnings.add("rank_info is not defined");
        } else if (song.rank_info.size() < 4) {
            warnings.add("rank_info should have at least 4 elements, calculating default values instead.");
        } else {
            int empty = 0;
            for (SimpleRankInfo rankInfo : song.rank_info) {
                if (rankInfo.rank_max == null) {
                    empty++;
                }
            }

            if (empty > 0) {
                errors.add("rank_info: rank_max is not specified for " + empty + " entries.");
            }
            Collections.sort(song.rank_info);
            Integer max = -1;
            for (int i = 0; i < song.rank_info.size() - 1; i++) {
                if (max.equals(song.rank_info.get(i).rank_max)) {
                    errors.add("rank_info: there's more than one entry for <" + max + ">");
                }
                max = song.rank_info.get(i).rank_max;
            }
        }
        this.errors.addAll(errors);
        return errors.size() > 0;
    }

    public boolean validateSongInfo(SimpleSong song) {
        List<String> errors = new ArrayList<>();

        if (song.song_info == null) {
            errors.add("song_info: song_info element is not defined.");
        } else if (song.song_info.size() == 0) {
            errors.add("song_info: the song_info element is empty.");
        } else {
            if (song.song_info.get(0).notes == null) {
                errors.add("song_info: notes element not defined.");
            } else if (song.song_info.get(0).notes.size() == 0) {
                errors.add("song_info: the beatmap doesn't contain notes. ");
            }
            if (song.song_info.get(0).notes_speed == null) {
                warnings.add("song_info: notes_speed element not defined, using default values for this difficulty");
            }
            Collections.sort(song.song_info.get(0).notes);
            int wrongDef = 0;
            for (SimpleNotesInfo notesInfo : song.song_info.get(0).notes) {
                if (notesInfo.timing_sec == null || notesInfo.effect == null || notesInfo.position == null || notesInfo.effect_value == null)
                    wrongDef++;
            }
            if (wrongDef > 0) {
                errors.add("song_info: <" + wrongDef + "> notes are defined incorrectly.");
            }
        }
        this.errors.addAll(errors);
        return errors.size() > 0;
    }
}
