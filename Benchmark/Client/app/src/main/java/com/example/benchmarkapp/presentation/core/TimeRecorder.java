package com.example.benchmarkapp.presentation.core;

public class TimeRecorder {

    private long startMilli, currMilli;

    public void startTimer(){
        startMilli = System.currentTimeMillis();
    }

    public long stopTimer(){
        currMilli = System.currentTimeMillis();

        return currMilli - startMilli;
    }
}
