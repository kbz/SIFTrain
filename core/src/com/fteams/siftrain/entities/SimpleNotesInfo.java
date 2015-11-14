package com.fteams.siftrain.entities;

import com.fteams.siftrain.util.SongUtils;

public class SimpleNotesInfo implements Comparable<SimpleNotesInfo>{
    public Double timing_sec;
    public Integer effect;
    public Double effect_value;
    public Integer position;


    @Override
    public int compareTo(SimpleNotesInfo o) {
        if (!o.timing_sec.equals(timing_sec))
            return Double.compare(timing_sec, o.timing_sec);
        return SongUtils.compare(position, o.position);
    }
}
