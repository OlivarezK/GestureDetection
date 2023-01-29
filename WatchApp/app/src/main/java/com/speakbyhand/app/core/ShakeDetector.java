package com.speakbyhand.app.core;

public class ShakeDetector {
    final int shakeThreshold;
    final float accelerationThreshold;
    final int windowSize;
    final LimitedDeque<Boolean> stack;
    public int acceleratingCount = 0;

    public ShakeDetector(int shakeThreshold, float accelerationThreshold, int windowSize) {
        this.shakeThreshold = shakeThreshold;
        this.accelerationThreshold = accelerationThreshold;
        this.windowSize = windowSize;
        this.stack = new LimitedDeque<>(windowSize);
    }

    public void update(float x, float y, float z){
        boolean accelerating = isAccelerating(x,y,z);
        if(stack.isFull() && stack.bottom()){
            acceleratingCount -= 1;
        }
        if(accelerating){
            acceleratingCount += 1;
        }
        stack.push(accelerating);
    }

    public void reset(){
        stack.clear();
        acceleratingCount = 0;
    }

    public boolean isAccelerating(float x, float y,float z){
        final double magnitudeSquared = x * x + y * y + z * z;
        return magnitudeSquared > accelerationThreshold;
    }

    public boolean isShaking(){
        return acceleratingCount >= shakeThreshold;
    }
}
