package com.fteams.siftrain.entities;

import com.fteams.siftrain.util.SongUtils;

public class SongFileInfo implements Comparable<SongFileInfo>{
    private String resourceName;
    public String song_name;
    public Integer difficulty;
    private String fileName;
    public String music_file;
    public String difficulty_name;
    private Long crc;

    public String toString() {
        return song_name + "[" + (difficulty_name == null ? SongUtils.getDifficulty(difficulty) : difficulty_name) + "]";
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public int compareTo(SongFileInfo o) {
        if (!song_name.equals(o.song_name))
        {
            return song_name.compareTo(o.song_name);
        }
        if (!o.getResourceName().equals(resourceName)) {
            return resourceName.compareTo(o.resourceName);
        }
        if (!difficulty.equals(o.difficulty)) {
            return difficulty.compareTo(o.difficulty);
        }
        return 0;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getCrc() {
        return crc;
    }

    public void setCrc(Long crc) {
        this.crc = crc;
    }
}
