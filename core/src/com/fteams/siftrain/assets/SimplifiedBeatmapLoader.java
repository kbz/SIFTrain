package com.fteams.siftrain.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.fteams.siftrain.entities.BeatmapDescription;
import com.google.gson.Gson;

public class SimplifiedBeatmapLoader extends AsynchronousAssetLoader<BeatmapDescription, SimplifiedBeatmapLoader.BeatmapParameter> {
    private BeatmapDescription beatmap;

    public SimplifiedBeatmapLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, BeatmapParameter parameter) {
        return null;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, BeatmapParameter parameter) {

        FileHandle handle = resolve(fileName);
        String jsonDefinition = handle.readString("UTF-8");
        try {
            beatmap = new Gson().fromJson(jsonDefinition, BeatmapDescription.class);
            beatmap.setFileName(fileName);
        } catch (Exception e)
        {
            beatmap = new BeatmapDescription();
            beatmap.song_name = "Error: Invalid JSON format "+ handle.file().getName();
            beatmap.difficulty = 1;
            beatmap.setFileName(fileName);
        }
        finally {
            beatmap.setResourceName(handle.nameWithoutExtension().replaceAll("_(easy|norma|hard|expert)$", ""));
        }
    }

    @Override
    public BeatmapDescription loadSync(AssetManager manager, String fileName, FileHandle file, BeatmapParameter parameter) {
        return beatmap;
    }

    public class BeatmapParameter extends AssetLoaderParameters<BeatmapDescription> {
    }
}
