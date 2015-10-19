package com.fteams.siftrain.entities;

import java.util.List;

public class SimpleSong extends SongFileInfo {
    public List<SimpleSongInfo> song_info;
    private Boolean valid;
    public Float lead_in;

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
}
