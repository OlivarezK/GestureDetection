package com.speakbyhand.app.core;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class DelimiterDetector  {
    private SensorManager sensorManager;
    private Sensor linearSensor;
    private final SensorEventListener eventListener;
    private final ShakeDetector shakeDetector = new ShakeDetector(15, 80, 50);

    public DelimiterDetector() {
        eventListener = new SensorEventListener() {
            @Override public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                shakeDetector.update(x,y,z);
            }

            @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
    }

    public boolean start(SensorManager sensorManager) {
        return start(sensorManager, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public boolean start(SensorManager sensorManager, int sensorDelay) {
        if (linearSensor != null) {
            return true;
        }

        linearSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        if (linearSensor != null) {
            this.sensorManager = sensorManager;
            sensorManager.registerListener(eventListener, linearSensor, sensorDelay);
        }
        return linearSensor != null;
    }

    public void stop() {
        if (linearSensor != null) {
            sensorManager.unregisterListener(eventListener, linearSensor);
            sensorManager = null;
            linearSensor = null;
            shakeDetector.reset();
        }
    }

    public boolean isDelimiterDetected(){
        return shakeDetector.isShaking();
    }
}