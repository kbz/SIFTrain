package com.fteams.siftrain.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.fteams.siftrain.assets.Assets;

public class SongLoader {
    private static final String SONGFILE_PREFIX = "beatmaps/soundfiles/";
    private static final String SONGFILE_MP3 = ".mp3";
    private static final String SONGFILE_OGG = ".ogg";
    private static final String SONGFILE_WAV = ".wav";

    public static Music loadSongFile() {
        if (Assets.selectedSong.music_file != null) {
            try {
                // try loading the file
                FileHandle handle = Gdx.files.absolute(Gdx.files.getExternalStoragePath() + SONGFILE_PREFIX + Assets.selectedSong.music_file);
                return Gdx.audio.newMusic(handle);
            } catch (Exception e) {
                // if it failed, try loading the file with a different extension (in case the extension was not specified)
                FileHandle handle = null;
                String path = Gdx.files.getExternalStoragePath() + SONGFILE_PREFIX + Assets.selectedSong.music_file.replaceAll("\\.[a-zA-Z0-9]+$","");
                try {
                    handle = Gdx.files.absolute(path + SONGFILE_OGG);
                    return Gdx.audio.newMusic(handle);
                } catch (Exception e1) {
                    try {
                        handle = Gdx.files.absolute(path + SONGFILE_WAV);
                        return Gdx.audio.newMusic(handle);
                    } catch (Exception e2) {
                        try {
                            handle = Gdx.files.absolute(path + SONGFILE_MP3);
                            return Gdx.audio.newMusic(handle);
                        } catch (Exception e3) {
                            return null;
                        }
                    }
                }
            }
        }
        String resourceName = Assets.selectedSong.getResourceName();
        String path = Gdx.files.getExternalStoragePath() + SONGFILE_PREFIX + resourceName;
        FileHandle handle = null;
        try {
            handle = Gdx.files.absolute(path + SONGFILE_OGG);
            return Gdx.audio.newMusic(handle);
        } catch (Exception e1) {
            try {
                handle = Gdx.files.absolute(path + SONGFILE_WAV);
                return Gdx.audio.newMusic(handle);
            } catch (Exception e2) {
                try {
                    handle = Gdx.files.absolute(path + SONGFILE_MP3);
                    return Gdx.audio.newMusic(handle);
                } catch (Exception e3) {
                    return null;
                }
            }
        }
    }
}
