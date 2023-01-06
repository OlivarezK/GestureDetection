package com.speakbyhand.app.core;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class GestureData {
    private final int AVERAGE_DATA_POINTS_COUNT = 250;
    private final List<ImuSensorReading> accReadings;
    private final List<ImuSensorReading> gyroReadings;
    private final List<Float> timeDataPoints;

    public GestureData(){
        accReadings = new ArrayList<>(AVERAGE_DATA_POINTS_COUNT);
        gyroReadings = new ArrayList<>(AVERAGE_DATA_POINTS_COUNT);
        timeDataPoints = new ArrayList<>(AVERAGE_DATA_POINTS_COUNT);
    }

    public GestureData(String csvString){
        this();
    }

    public void addAccelerometerData(float x, float y, float z){
        accReadings.add(new ImuSensorReading(x,y,z));
    }

    public void addGyroscopeData(float x, float y, float z){
        accReadings.add(new ImuSensorReading(x,y,z));

    }

    public void addTime(float milliseconds){
        timeDataPoints.add(milliseconds);
    }

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

    public float[] toArray(){
        int dataPointCount = getCount();
        float[] data = new float[dataPointCount * 7];
        for (int i = 0; i < dataPointCount; i++) {
            float timeData = timeDataPoints.get(i);
            ImuSensorReading accReading = accReadings.get(i);
            ImuSensorReading gyroReading = gyroReadings.get(i);

            data[i] = timeData;
            data[i+1] = accReading.x;
            data[i+2] = accReading.y;
            data[i+3] = accReading.z;
            data[i+4] = gyroReading.x;
            data[i+5] = gyroReading.y;
            data[i+6] = gyroReading.z;
        }
        return data;
    }

    public int getCount(){
        return accReadings.size();
    }

}
