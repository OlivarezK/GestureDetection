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
    private GestureData gestureData = new GestureData();
    private long startMilli;
    private final SensorManager accManager, gyroManager;
    private final Sensor accSensor, gyroSensor;
    private final SensorEventListener accListener, gyroListener;
    private final List<Float> filtered_data;

    private float g_x = 0f, g_y = 0f, g_z = 9.8f;
    private float new_a_x, new_a_y, new_a_z;
    private float alpha = 0.9f;
//    private final PauseDetector pauseDetector = new PauseDetector();

    public GestureDataRecorder(Context onCreateContext) {
        accManager = (SensorManager) onCreateContext.getSystemService(Context.SENSOR_SERVICE);
        gyroManager = (SensorManager) onCreateContext.getSystemService(Context.SENSOR_SERVICE);
        accSensor = accManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = gyroManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        filtered_data = new ArrayList<>(3);
//        linearSensor = gyroManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        accListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                // use when using filtered model
//                filtered_data.clear();
//                filterData(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
//                gestureData.addAccelerometerData(filtered_data.get(0), filtered_data.get(1), filtered_data.get(2));

                // use when not using filtered model
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
//        linearListener = new SensorEventListener() {
//            @Override
//            public void onSensorChanged(SensorEvent sensorEvent) {
//                pauseDetector.update(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
//                Log.i("Count",String.valueOf(pauseDetector.pauseCount));
//            }
//
//            @Override
//            public void onAccuracyChanged(Sensor sensor, int i) {
//            }
//        };
    }

//    public boolean isPaused(){
//        return pauseDetector.isPaused();
//    }

//    class PauseDetector{
//        final int pauseThreshold = 40;
//        final float accelerationThreshold = 0.35f;
//        final LimitedStack<Boolean> stack = new LimitedStack<>(50);
//        public int pauseCount = 0;
//
//        public void update(float x, float y,float z){
//            boolean accelerating = isAccelerating(x,y,z);
//            if(stack.isFull() && stack.bottom() && pauseCount>0){
//                pauseCount -= 1;
//            }
//            if(!accelerating){
//                pauseCount += 1;
//            }
//            stack.push(accelerating);
//        }
//
//        public void reset(){
//            stack.clear();
//            pauseCount = 0;
//        }
//
//        public boolean isAccelerating(float x, float y,float z){
//            final double magnitudeSquared = x * x + y * y + z * z;
//            return magnitudeSquared > accelerationThreshold;
//        }
//
//        public boolean isPaused(){
//            return pauseCount > pauseThreshold;
//        }
//    }

    public void start(){
        startMilli = System.currentTimeMillis();
        accManager.registerListener(accListener, accSensor, SensorManager.SENSOR_DELAY_FASTEST);
        gyroManager.registerListener(gyroListener, gyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
//        new CountDownTimer(1000, 10){
//
//            @Override
//            public void onTick(long l) {
//
//            }
//
//            @Override
//            public void onFinish() {
//                gyroManager.registerListener(linearListener, linearSensor, SensorManager.SENSOR_DELAY_FASTEST);
//            }
//        }.start();
    }

    public void stop() {
        accManager.unregisterListener(accListener);
        gyroManager.unregisterListener(gyroListener);
//        gyroManager.unregisterListener(linearListener);
//        pauseDetector.reset();
    }

    private void filterData(float a_x, float a_y, float a_z){
        g_x = alpha * g_x + (1 - alpha) * a_x;
        g_y = alpha * g_y + (1 - alpha) * a_y;
        g_z = alpha * g_z + (1 - alpha) * a_z;

        new_a_x = a_x - g_x;
        new_a_y = a_y - g_y;
        new_a_z = a_z - g_z;

        filtered_data.add(new_a_x);
        filtered_data.add(new_a_y);
        filtered_data.add(new_a_z);
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
