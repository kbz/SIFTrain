package com.fteams.siftrain.entities;

public class SimpleRankInfo implements Comparable<SimpleRankInfo>{
    public Integer rank_max;

    public SimpleRankInfo(Integer rank_max)
    {
        this.rank_max = rank_max;
    }
    @Override
    public int compareTo(SimpleRankInfo o) {
        if (o == null)
        {
            return -1;
        }
        return Integer.compare(rank_max, o.rank_max);
    }
}
