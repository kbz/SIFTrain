package com.fteams.siftrain.entities;

import com.badlogic.gdx.utils.Array;
import com.fteams.siftrain.assets.GlobalConfiguration;
import com.fteams.siftrain.util.SongUtils;

public class SimpleSongGroup implements Comparable<SimpleSongGroup> {
    public Array<SongFileInfo> songs;
    public String song_name;
    public String resource_name;

    public String toString()
    {
        return song_name + " (" + songs.size + ")";
    }

    @Override
    public int compareTo(SimpleSongGroup o) {
        if (GlobalConfiguration.sortMode == SongUtils.SORTING_MODE_FILE_NAME)
        {
            if (!o.resource_name.equals(resource_name)) {
                return resource_name.compareTo(o.resource_name);
            }
            if (!song_name.equals(o.song_name))
            {
                return song_name.compareTo(o.song_name);
            }
        }
        else if (GlobalConfiguration.sortMode == SongUtils.SORTING_MODE_SONG_NAME)
        {
            if (!song_name.equals(o.song_name))
            {
                return song_name.compareTo(o.song_name);
            }
            if (!o.resource_name.equals(resource_name)) {
                return resource_name.compareTo(o.resource_name);
            }
        }
        return 0;
    }
}
