package com.example.smgesturerecognizer.dtw;

import com.example.smgesturerecognizer.timeseries.TimeSeries;

public class FullWindow extends SearchWindow{
    // CONSTRUCTOR
    public FullWindow(TimeSeries tsI, TimeSeries tsJ)
    {
        super(tsI.size(), tsJ.size());

        for (int i=0; i<tsI.size(); i++)
        {
            super.markVisited(i, minJ());
            super.markVisited(i, maxJ());
        }  // end for loop
    }  // end CONSTRUCTOR
}
