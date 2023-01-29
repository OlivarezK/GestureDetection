package com.speakbyhand.app.core;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GestureDataRecorder {
    private long startMilli;
    public GestureData gestureData = new GestureData();
    private PauseDetector pauseDetector = new PauseDetector();
    private final SensorManager accManager, gyroManager, linearManager;
    private final Sensor accSensor, gyroSensor, linearSensor;
    private final SensorEventListener accListener, gyroListener, linearListener;

    public GestureDataRecorder(Context onCreateContext) {
        accManager = (SensorManager) onCreateContext.getSystemService(Context.SENSOR_SERVICE);
        gyroManager = (SensorManager) onCreateContext.getSystemService(Context.SENSOR_SERVICE);
        linearManager = (SensorManager) onCreateContext.getSystemService(Context.SENSOR_SERVICE);

        accSensor = accManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = gyroManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        linearSensor = linearManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        accListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                gestureData.addAccelerometerData(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
                recordTimeMilliseconds();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        gyroListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                gestureData.addGyroscopeData(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        linearListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                pauseDetector.update(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

    }

    public boolean isPaused(){ return pauseDetector.isPaused(); }

    public void start(){
        startMilli = System.currentTimeMillis();
        accManager.registerListener(accListener, accSensor, SensorManager.SENSOR_DELAY_FASTEST);
        gyroManager.registerListener(gyroListener, gyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
        linearManager.registerListener(linearListener, linearSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void stop() {
        accManager.unregisterListener(accListener);
        gyroManager.unregisterListener(gyroListener);
        linearManager.unregisterListener(linearListener);
        pauseDetector.reset();
    }

    public void reset(){
        gestureData = new GestureData();
    }

    private void recordTimeMilliseconds() {
        long currMilli = System.currentTimeMillis();
        gestureData.addTime((float) (currMilli - startMilli));
    }

    public GestureData getData(){
        return gestureData;
    }

}
