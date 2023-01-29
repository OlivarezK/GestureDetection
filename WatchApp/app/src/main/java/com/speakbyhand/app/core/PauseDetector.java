package com.speakbyhand.app.core;

import android.util.Log;

class PauseDetector {
    final int pauseThreshold = 3;
    final float accelerationThreshold = 0.4f;
    final LimitedDeque<Boolean> stack = new LimitedDeque<>(50);
    public int pauseCount = 0;

    public void update(float x, float y, float z){
        boolean accelerating = isAccelerating(x, y, z);
        if(stack.isFull() && stack.bottom() && pauseCount>0){
            pauseCount -= 1;
        }
        if(!accelerating){
            pauseCount += 1;
        }
        stack.push(accelerating);
    }

    public void reset(){
        stack.clear();
        pauseCount = 0;
    }

    public boolean isAccelerating(float x, float y,float z){
        final double magnitudeSquared = x * x + y * y + z * z;
        return magnitudeSquared > accelerationThreshold;
    }

    public boolean isPaused(){
        return pauseCount > pauseThreshold;
    }
}
