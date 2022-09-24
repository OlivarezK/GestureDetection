package com.example.smgesturerecognizer.dtw;

public class TimeWarpInfo {
    // PRIVATE DATA
    private final double distance;
    private final WarpPath path;



    // CONSTRUCTOR
    TimeWarpInfo(double dist, WarpPath wp)
    {
        distance = dist;
        path = wp;
    }


    public double getDistance()
    {
        return distance;
    }


    public WarpPath getPath()
    {
        return path;
    }


    public String toString()
    {
        return "(Warp Distance=" + distance + ", Warp Path=" + path + ")";
    }
}
