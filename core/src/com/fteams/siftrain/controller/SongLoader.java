package com.fteams.siftrain.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;

public class SongLoader {
    private static final String SONGFILE_PREFIX = "beatmaps/soundfiles//";
    private static final String SONGFILE_MP3 = ".mp3";
    private static final String SONGFILE_OGG = ".ogg";
    private static final String SONGFILE_WAV = ".wav";

    public static Music loadSongFile(String resourceName) {
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
                    throw e3;
                }
            }
        }

    }
}
