package com.fteams.siftrain.entities;

import com.fteams.siftrain.assets.GlobalConfiguration;
import com.fteams.siftrain.util.SongUtils;

public class SongFileInfo implements Comparable<SongFileInfo> {
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
        if (GlobalConfiguration.sortMode == SongUtils.SORTING_MODE_FILE_NAME) {
            if (!o.getResourceName().equals(resourceName)) {
                return (GlobalConfiguration.sortOrder == SongUtils.SORTING_MODE_ASCENDING ? 1 : -1) * resourceName.compareTo(o.getResourceName());
            }
            if (!song_name.equals(o.song_name)) {
                return (GlobalConfiguration.sortOrder == SongUtils.SORTING_MODE_ASCENDING ? 1 : -1) * song_name.compareTo(o.song_name);
            }
        } else if (GlobalConfiguration.sortMode == SongUtils.SORTING_MODE_SONG_NAME) {
            if (!song_name.equals(o.song_name)) {
                return (GlobalConfiguration.sortOrder == SongUtils.SORTING_MODE_ASCENDING ? 1 : -1) * song_name.compareTo(o.song_name);
            }
            if (!o.getResourceName().equals(resourceName)) {
                return (GlobalConfiguration.sortOrder == SongUtils.SORTING_MODE_ASCENDING ? 1 : -1) * resourceName.compareTo(o.getResourceName());
            }
        }
        // always check difficulty last to keep them in order
        if (!difficulty.equals(o.difficulty)) {
            return (GlobalConfiguration.sortOrder == SongUtils.SORTING_MODE_ASCENDING ? 1 : -1) * difficulty.compareTo(o.difficulty);
        }
        if (difficulty_name != null && o.difficulty_name != null && !difficulty_name.equals(o.difficulty_name)) {
            return (GlobalConfiguration.sortOrder == SongUtils.SORTING_MODE_ASCENDING ? 1 : -1) * difficulty_name.compareTo(o.difficulty_name);
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
