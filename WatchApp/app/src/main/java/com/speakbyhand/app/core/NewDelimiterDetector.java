package com.speakbyhand.app.core;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

public class NewDelimiterDetector implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private final Detector detector = new Detector();

    public boolean start(SensorManager sensorManager) {
        return start(sensorManager, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public boolean start(SensorManager sensorManager, int sensorDelay) {
        if (accelerometer != null) {
            return true;
        }

        accelerometer = sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);


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



    class Detector{
        final int shakeThreshold = 700;
        final float accelerationThreshold = 150;
        final LimitedStack<Boolean> stack = new LimitedStack<>(2000);
        int acceleratingCount = 0;

        public void update(float x, float y,float z){
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

    static class SampleQueue {

        private static final long MAX_WINDOW_SIZE = 500000000; // 0.5s
        private static final long MIN_WINDOW_SIZE = MAX_WINDOW_SIZE >> 1; // 0.25s

        private static final int MIN_QUEUE_SIZE = 4;

        private final SamplePool pool = new SamplePool();

        private Sample oldest;
        private Sample newest;
        private int sampleCount;
        private int acceleratingCount;

        void add(long timestamp, boolean accelerating) {
            purge(timestamp - MAX_WINDOW_SIZE);

            Sample added = pool.acquire();
            added.timestamp = timestamp;
            added.accelerating = accelerating;
            added.next = null;
            if (newest != null) {
                newest.next = added;
            }
            newest = added;
            if (oldest == null) {
                oldest = added;
            }

            sampleCount++;
            if (accelerating) {
                acceleratingCount++;
            }
        }

        void clear() {
            while (oldest != null) {
                Sample removed = oldest;
                oldest = removed.next;
                pool.release(removed);
            }
            newest = null;
            sampleCount = 0;
            acceleratingCount = 0;
        }

        void purge(long cutoff) {
            while (sampleCount >= MIN_QUEUE_SIZE
                    && oldest != null && cutoff - oldest.timestamp > 0) {
                Sample removed = oldest;
                if (removed.accelerating) {
                    acceleratingCount--;
                }
                sampleCount--;

                oldest = removed.next;
                if (oldest == null) {
                    newest = null;
                }
                pool.release(removed);
            }
        }

        List<Sample> asList() {
            List<Sample> list = new ArrayList<>();
            Sample s = oldest;
            while (s != null) {
                list.add(s);
                s = s.next;
            }
            return list;
        }

        boolean isShaking() {
            return newest != null
                    && oldest != null
                    && newest.timestamp - oldest.timestamp >= MIN_WINDOW_SIZE
                    && acceleratingCount >= (sampleCount >> 1) + (sampleCount >> 2);
        }
    }

    static class Sample {
        long timestamp;

        boolean accelerating;

        Sample next;
    }

    static class SamplePool {
        private Sample head;

        Sample acquire() {
            Sample acquired = head;
            if (acquired == null) {
                acquired = new Sample();
            } else {
                head = acquired.next;
            }
            return acquired;
        }

        void release(Sample sample) {
            sample.next = head;
            head = sample;
        }
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}