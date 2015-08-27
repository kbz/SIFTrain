package com.fteams.siftrain.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.fteams.siftrain.assets.Assets;

public class SongLoader {
    private static final String SONGFILE_PREFIX = "beatmaps/soundfiles/";
    private static final String[] SONGFILE_PRIO = {".ogg", ".wav", ".mp3"};

    public static Music loadSongByName(String name) {
        try {
            // try loading the file
            FileHandle handle = Gdx.files.absolute(Gdx.files.getExternalStoragePath() + SONGFILE_PREFIX + name);
            return Gdx.audio.newMusic(handle);
        } catch(Exception e) {
            // if it failed, try loading the file with a different extension (in case the extension was not specified)
            FileHandle handle = null;
            String path = Gdx.files.getExternalStoragePath() + SONGFILE_PREFIX + name.replaceAll("\\.[a-zA-Z0-9]+$","");

            for(String ext : SONGFILE_PRIO) {
                try {
                    handle = Gdx.files.absolute(path + ext);
                    return Gdx.audio.newMusic(handle);
                } catch(Exception e2) {
                    continue;
                }
            }

            return null;
        }
    }

    public static Music loadSongFile() {
        Music result = null;

        if(Assets.selectedSong.music_file != null)
            result = loadSongByName(Assets.selectedSong.music_file);

        if(result == null)
            result = loadSongByName(Assets.selectedSong.getResourceName());

        return result;
    }
}
