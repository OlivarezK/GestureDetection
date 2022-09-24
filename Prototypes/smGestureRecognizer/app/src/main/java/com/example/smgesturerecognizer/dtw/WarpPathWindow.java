package com.example.smgesturerecognizer.dtw;

public class WarpPathWindow extends SearchWindow{
    // CONSTANTS
    private final static int defaultRadius = 0;



    // CONSTRUCTORS
    public WarpPathWindow(WarpPath path, int searchRadius)
    {
        super(path.get(path.size()-1).getCol()+1, path.get(path.size()-1).getRow()+1);

        for (int p=0; p<path.size(); p++)
            super.markVisited(path.get(p).getCol(), path.get(p).getRow());

        super.expandWindow(searchRadius);
    }  // end Constructor
}
