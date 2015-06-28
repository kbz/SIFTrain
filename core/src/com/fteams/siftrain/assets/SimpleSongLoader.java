package com.fteams.siftrain.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.fteams.siftrain.entities.SimpleNotesInfo;
import com.fteams.siftrain.entities.SimpleRankInfo;
import com.fteams.siftrain.entities.SimpleSong;
import com.google.gson.Gson;

public class SimpleSongLoader extends AsynchronousAssetLoader<SimpleSong, SimpleSongLoader.SimpleSongParameter> {
    private SimpleSong song;

    public SimpleSongLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, SimpleSongLoader.SimpleSongParameter parameter) {
        FileHandle handle = resolve(fileName);
        String jsonDefinition = handle.readString("UTF-8");
        try {
            song = new Gson().fromJson(jsonDefinition, SimpleSong.class);
            String validationResult = validateFields(song);
            song.setValid(true);
            if (validationResult != null)
            {
                Gdx.app.log("MAPLOADER",handle.file().getName() + " " + validationResult);
                song = new SimpleSong();
                song.song_name = "Error: Beatmap format invalid. (" + handle.file().getName() +")";
                song.difficulty = 1;
                song.setValid(false);
            }
        } catch (Exception e) {
            song = new SimpleSong();
            song.song_name = "Invalid JSON Format: " + handle.file().getName();
            song.difficulty = 1;
            song.setValid(false);
        } finally {
            song.setResourceName(handle.file().getName().replaceAll("(_easy)|(_normal)|(_hard)|(_expert)|(\\.rs)", ""));
        }
    }

    private String validateFields(SimpleSong song) {
        if (song.rank_info.length != 5)
            return "rank_info must have 5 elements";
        for (SimpleRankInfo rankInfo : song.rank_info)
        {
            if (rankInfo.rank == null || rankInfo.rank_max == null)
                return "verify rank format. ";
        }
        int previousRank = 0;
        for (int i = 0; i < song.rank_info.length-1; i++)
        {
            SimpleRankInfo sri = song.rank_info[i];
            if (previousRank >= sri.rank_max)
            {
                return "ranks are not ordered.";
            }
            previousRank = sri.rank_max;
        }
        if (song.song_info.length < 1)
            return "song_info is empty";
        if (song.song_info[0].notes_speed == null)
            return "notes_speed is empty";
        if (song.song_info[0].notes.length == 0)
            return "notes is empty";
        for (SimpleNotesInfo notesInfo : song.song_info[0].notes)
        {
            if (notesInfo.timing_sec == null || notesInfo.effect == null || notesInfo.position == null || notesInfo.effect_value == null)
                return "note format invalid. ";
        }
        if (song.difficulty == null)
            return "difficulty is not set";
        return null;
    }

    @Override
    public SimpleSong loadSync(AssetManager manager, String fileName, FileHandle file, SimpleSongLoader.SimpleSongParameter parameter) {
        SimpleSong song = this.song;
        this.song = null;
        return song;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, SimpleSongLoader.SimpleSongParameter parameter) {
        return null;
    }

    static public class SimpleSongParameter extends AssetLoaderParameters<SimpleSong> {
    }
}
