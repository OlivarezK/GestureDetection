package com.speakbyhand.datacollectorclient.core;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

class ImuSensorReading {
    public float x;
    public float y;
    public float z;

    public ImuSensorReading(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

public class GestureDataRecorder {
    private final SensorManager accManager, gyroManager;
    private final Sensor accSensor, gyroSensor;
    private final int AVERAGE_DATA_POINTS_COUNT = 200;
    private final ArrayList<ImuSensorReading> accDataPoints = new ArrayList<>(AVERAGE_DATA_POINTS_COUNT);
    private final ArrayList<ImuSensorReading> gyroDataPoints = new ArrayList<>(AVERAGE_DATA_POINTS_COUNT);
    private final ArrayList<Float> timeDataPoints = new ArrayList<>(AVERAGE_DATA_POINTS_COUNT);

    private long startMilli;

    private final SensorEventListener accListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            accDataPoints.add(new ImuSensorReading(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]));
            // does not need to be removed even when not used
            recordTimeMilliseconds();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };
    private final SensorEventListener gyroListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            gyroDataPoints.add(new ImuSensorReading(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    public GestureDataRecorder(Context onCreateContext) {
        accManager = (SensorManager) onCreateContext.getSystemService(Context.SENSOR_SERVICE);
        gyroManager = (SensorManager) onCreateContext.getSystemService(Context.SENSOR_SERVICE);
        accSensor = accManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = gyroManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    public void start() {
        startMilli = System.currentTimeMillis();
        accManager.registerListener(accListener, accSensor, SensorManager.SENSOR_DELAY_FASTEST);
        gyroManager.registerListener(gyroListener, gyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void stop() {
        accManager.unregisterListener(accListener);
        gyroManager.unregisterListener(gyroListener);
    }

    public void reset() {
        accDataPoints.clear();
        gyroDataPoints.clear();
        timeDataPoints.clear();
    }

    private void recordTimeMilliseconds() {
        long currMilli = System.currentTimeMillis();
        timeDataPoints.add((float) (currMilli - startMilli));
    }

    public String getDataAsCsvString() {
        StringBuilder csvContentBuilder = new StringBuilder();
        for (int i = 0; i < accDataPoints.size(); i++) {
            ImuSensorReading accSample = accDataPoints.get(i);
            ImuSensorReading gyroSample = gyroDataPoints.get(i);
            float currentTime = timeDataPoints.get(i);

            csvContentBuilder.append(String.join(",",
                    String.valueOf(currentTime),
                    String.valueOf(accSample.x),
                    String.valueOf(accSample.y),
                    String.valueOf(accSample.z),
                    String.valueOf(gyroSample.x),
                    String.valueOf(gyroSample.y),
                    String.valueOf(gyroSample.z)));
            csvContentBuilder.append("\n");
        }
        return csvContentBuilder.toString();
    }
}
