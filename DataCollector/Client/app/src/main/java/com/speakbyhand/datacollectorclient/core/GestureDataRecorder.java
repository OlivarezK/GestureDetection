package com.speakbyhand.datacollectorclient.core;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import java.util.ArrayList;

public class GestureDataRecorder {
    private SensorManager a_Manager, g_Manager;
    private Sensor a_Sensor, g_Sensor;

    private ArrayList<String> a_data = new ArrayList<String>();
    private ArrayList<String> g_data = new ArrayList<String>();
    private ArrayList<String> time_data = new ArrayList<String>();

    private long startMilli, currMilli;

    private final SensorEventListener a_Listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            a_data.add(String.valueOf(sensorEvent.values[0]));
            a_data.add(String.valueOf(sensorEvent.values[1]));
            a_data.add(String.valueOf(sensorEvent.values[2]));

            // does not need to be removed even when not used
            getTimeData();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private final SensorEventListener g_Listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            g_data.add(String.valueOf(sensorEvent.values[0]));
            g_data.add(String.valueOf(sensorEvent.values[1]));
            g_data.add(String.valueOf(sensorEvent.values[2]));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    // initializes necessary components
    // pass onCreate function's Context as argument
    public void initializeGestureComponents(Context onCreateContext){
        a_Manager = (SensorManager) onCreateContext.getSystemService(Context.SENSOR_SERVICE);
        g_Manager = (SensorManager) onCreateContext.getSystemService(Context.SENSOR_SERVICE);
        a_Sensor = a_Manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        g_Sensor = g_Manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    // starts recording gesture data
    public void recordGesture(){

        // used in getTimeData()
        // does not need to be removed even when not used
        startMilli = System.currentTimeMillis();

        // record accelerometer and gyroscope data
        a_Manager.registerListener(a_Listener, a_Sensor, SensorManager.SENSOR_DELAY_FASTEST);
        g_Manager.registerListener(g_Listener, g_Sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    // stops recording gesture data
    public void unregisterListeners(){

        // unregister gesture listeners
        a_Manager.unregisterListener(a_Listener);
        g_Manager.unregisterListener(g_Listener);
    }

    // clear time, accelerometer, and gyroscope data
    // don't forget to clear data after a recording session
    // if data will be merged after recording, clearing must be done after merging
    public void clearData(){
        a_data.clear();
        g_data.clear();
        time_data.clear();
    }

    // records time data in milliseconds
    public void getTimeData(){
        currMilli = System.currentTimeMillis();
        time_data.add(String.valueOf(currMilli-startMilli) + ",");
    }

    // merges accelerometer and gyroscope data
    // TODO: try better approach (if possible)
    public float[] mergeData(){
        float[] merged_data = new float[dataLen()*2];
        int ctr = 0, a_ctr = 0, g_ctr = 0;
        for (int i = 0; i < dataLen(); i++){

            if (ctr == 6){
                ctr = 0;
            }

            if (ctr < 3){
                merged_data[i] = Float.parseFloat(a_data.get(a_ctr));
                a_ctr ++;
            }else{
                merged_data[i] = Float.parseFloat(g_data.get(a_ctr));
                g_ctr ++;
            }

            ctr ++;
        }

        return merged_data;
    }

    // merges time, accelerometer, and gyroscope data in csv format
    public String mergeToCsv(){
        String merged_data = "";

        // merge time, accelerometer, and gyroscope data in csv format
        int ctr = 0;
        int start = 0, end = 2;

        for (int i = 0; i < dataLen(); i++){

            // add time data at the beginning of each line
            if (ctr == 0){
                merged_data += time_data.get(i/3) + ",";
            }

            // add a_data
            merged_data += a_data.get(i) + ",";
            ctr ++;

            // concatenate 3 g_data every 3rd a_data
            if (ctr == 3){
                ctr = 0;

                for (int j = start; j <= end; j++){
                    if (j == end){
                        merged_data += g_data.get(j) + "\n"; // newline without ',' every third g_data
                    }else{
                        merged_data += g_data.get(j) + ",";
                    }
                }

                start += 3;
                end += 3;
            }
        }

        return merged_data;
    }

    // checks and returns length of accelerometer and gyroscope data
    private int dataLen(){
        int data_len = 0;

        // check if accelerometer and gyro data have same no. of data
        // if not, get smaller size
        if (a_data.size() == g_data.size() ||
                a_data.size() < g_data.size()){

            data_len = a_data.size();
        }else{

            data_len = g_data.size();
        }

        return data_len;
    }
}
