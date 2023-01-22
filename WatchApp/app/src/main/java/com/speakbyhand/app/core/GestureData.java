package com.speakbyhand.app.core;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GestureData {
    private final List<ImuSensorReading> accReadings;
    private final List<ImuSensorReading> gyroReadings;
    private final List<Float> timeDataPoints;

    public final static int dataPointCount = 297;


    public GestureData() {
        int AVERAGE_DATA_POINTS_COUNT = 300;
        accReadings = new ArrayList<>(AVERAGE_DATA_POINTS_COUNT);
        gyroReadings = new ArrayList<>(AVERAGE_DATA_POINTS_COUNT);
        timeDataPoints = new ArrayList<>(AVERAGE_DATA_POINTS_COUNT);
    }

    public GestureData(String csvString) {
        this();

        for (String row : csvString.trim().split("\n")) {
            String[] rowValues = row.split(",");
            timeDataPoints.add(Float.parseFloat(rowValues[0]));
            accReadings.add(new ImuSensorReading(
                    Float.parseFloat(rowValues[1]),
                    Float.parseFloat(rowValues[2]),
                    Float.parseFloat(rowValues[3])
            ));
            gyroReadings.add(new ImuSensorReading(
                    Float.parseFloat(rowValues[4]),
                    Float.parseFloat(rowValues[5]),
                    Float.parseFloat(rowValues[6])
            ));
        }

    }

    public void addAccelerometerData(float x, float y, float z) {
        accReadings.add(new ImuSensorReading(x, y, z));
    }

    public void addGyroscopeData(float x, float y, float z) {
        gyroReadings.add(new ImuSensorReading(x, y, z));
    }

    public void addTime(float milliseconds) {
        timeDataPoints.add(milliseconds);
    }

    static class ImuSensorReading {
        public float x;
        public float y;
        public float z;

        public ImuSensorReading(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ImuSensorReading that = (ImuSensorReading) o;
            return Float.compare(that.x, x) == 0 && Float.compare(that.y, y) == 0 && Float.compare(that.z, z) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }
    }

    // TODO: Create unit tests for this
    public float[] toArray() {
        float[] data = new float[dataPointCount * 6];
        // TODO: BUG - size of input of model and number of collected data points may not be the same size
        for (int index = 0; index < Math.min(getCount(), dataPointCount); index++) {
            ImuSensorReading accReading = accReadings.get(index);
            ImuSensorReading gyroReading = gyroReadings.get(index);

            int relativeIndex = index * 6;
            data[relativeIndex] = accReading.x;
            data[relativeIndex + 1] = accReading.y;
            data[relativeIndex + 2] = accReading.z;
            data[relativeIndex + 3] = gyroReading.x;
            data[relativeIndex + 4] = gyroReading.y;
            data[relativeIndex + 5] = gyroReading.z;
        }
        return data;
    }

    public int getCount() {
        return Math.min(accReadings.size(), gyroReadings.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GestureData that = (GestureData) o;

        for (int i = 0; i < this.accReadings.size(); i++) {
            if (!Objects.equals(this.accReadings.get(i), that.accReadings.get(i))) {
                return false;
            }
        }

        for (int i = 0; i < this.gyroReadings.size(); i++) {
            if (!Objects.equals(this.gyroReadings.get(i), that.gyroReadings.get(i))) {
                return false;
            }
        }

        for (int i = 0; i < this.timeDataPoints.size(); i++) {
            if (!Objects.equals(this.timeDataPoints.get(i), that.timeDataPoints.get(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accReadings, gyroReadings, timeDataPoints);
    }
}
