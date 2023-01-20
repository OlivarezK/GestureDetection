package com.speakbyhand.app.core;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class DelimiterDetector implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private final Detector detector = new Detector(15, 80, 50);

    public boolean start(SensorManager sensorManager) {
        return start(sensorManager, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public boolean start(SensorManager sensorManager, int sensorDelay) {
        if (accelerometer != null) {
            return true;
        }

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        if (accelerometer != null) {
            this.sensorManager = sensorManager;
            sensorManager.registerListener(this, accelerometer, sensorDelay);
        }
        return accelerometer != null;
    }

    public void stop() {
        if (accelerometer != null) {
            sensorManager.unregisterListener(this, accelerometer);
            sensorManager = null;
            accelerometer = null;
            detector.reset();
        }
    }

    public boolean isDelimiterDetected(){
        return detector.isShaking();
    }

    @Override public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[0];
        float z = event.values[0];
        detector.update(x,y,z);
    }

    static class Detector{
        final int shakeThreshold;
        final float accelerationThreshold;
        final int windowSize;
        final LimitedDeque<Boolean> stack;
        public int acceleratingCount = 0;

        public Detector(int shakeThreshold, float accelerationThreshold, int windowSize) {
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
            return acceleratingCount > shakeThreshold;
        }
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}