package com.fteams.siftrain.entities;

import com.fteams.siftrain.util.SongUtils;

public class BeatmapDescription implements Comparable<BeatmapDescription> {
    public String song_name;
    public Integer difficulty;
    private String fileName;
    private String resourceName;

    public String toString() {
        return song_name + " [" + SongUtils.getDifficulty(difficulty) + "]";
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public int compareTo(BeatmapDescription o) {
        if (!o.getResourceName().equals(resourceName)) {
            return resourceName.compareTo(o.resourceName);
        }
        if (!difficulty.equals(o.difficulty)) {
            return difficulty.compareTo(o.difficulty);
        }
        return 0;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceName() {
        return resourceName;
    }
}
