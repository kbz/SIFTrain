package com.fteams.siftrain.entities;

public class SimpleNotesInfo implements Comparable<SimpleNotesInfo>{
    public Double timing_sec;
    public Integer effect;
    public Double effect_value;
    public Integer position;


    @Override
    public int compareTo(SimpleNotesInfo o) {
        if (!o.timing_sec.equals(timing_sec))
            return Double.compare(timing_sec, o.timing_sec);
        return Integer.compare(position, o.position);
    }
}
