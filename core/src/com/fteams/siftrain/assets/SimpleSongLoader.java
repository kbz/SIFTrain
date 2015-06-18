package com.fteams.siftrain.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
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
        song = new Gson().fromJson(jsonDefinition, SimpleSong.class);
        song.setResourceName(handle.file().getName().replaceAll("(_easy)|(_normal)|(_hard)|(_expert)|(\\.rs)", ""));
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
