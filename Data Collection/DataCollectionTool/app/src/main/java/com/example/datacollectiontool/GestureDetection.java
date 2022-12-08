package com.example.datacollectiontool;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GestureDetection{

    private SensorManager a_Manager, g_Manager;
    private Sensor a_Sensor, g_Sensor;

    private String a_data = "", g_data = "", time_data = "";

    private float a_x, a_y, a_z, g_x, g_y, g_z;
    private long startMilli, currMilli;

    private SensorEventListener a_Listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            a_x = sensorEvent.values[0];
            a_y = sensorEvent.values[1];
            a_z = sensorEvent.values[2];

            // does not need to be removed even when not used
            getTimeData();

            a_data += String.valueOf(a_x) + "," + String.valueOf(a_y) + "," + String.valueOf(a_z) + ",";
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };
    private SensorEventListener g_Listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            g_x = sensorEvent.values[0];
            g_y = sensorEvent.values[1];
            g_z = sensorEvent.values[2];

            g_data += String.valueOf(g_x) + "," + String.valueOf(g_y) + "," + String.valueOf(g_z) + ",";
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
        a_Manager.registerListener(a_Listener, a_Sensor, a_Manager.SENSOR_DELAY_NORMAL);
        g_Manager.registerListener(g_Listener, g_Sensor, g_Manager.SENSOR_DELAY_NORMAL);
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
        a_data = "";
        g_data = "";
        time_data = "";
    }

    // records time data in milliseconds
    public void getTimeData(){
        currMilli = System.currentTimeMillis();
        time_data += String.valueOf(currMilli-startMilli) + ",";
    }

    // merges accelerometer and gyroscope data
    // TODO: try better approach (if possible)
    public float[] mergeData(){

        float[] merged_data = new float[dataLen()*2];

        String[] split_a = a_data.split(",");
        String[] split_g = g_data.split(",");

        int ctr = 0, a_ctr = 0, g_ctr = 0;
        for (int i = 0; i < dataLen(); i++){

            if (ctr == 6){
                ctr = 0;
            }

            if (ctr < 3){
                merged_data[i] = Float.parseFloat(split_a[a_ctr]);
                a_ctr ++;
            }else{
                merged_data[i] = Float.parseFloat(split_g[g_ctr]);
                g_ctr ++;
            }

            ctr ++;
        }

        return merged_data;
    }

    // merges time, accelerometer, and gyroscope data in csv format
    public String mergeToCsv(){
        String merged_data = "";

        String[] split_a = a_data.split(",");
        String[] split_g = g_data.split(",");
        String[] split_time = time_data.split(",");

        // merge time, accelerometer, and gyroscope data in csv format
        int ctr = 0;
        int start = 0, end = 2;

        for (int i = 0; i < dataLen(); i++){

            // add time data at the beginning of each line
            if (ctr == 0){
                merged_data += split_time[i/3] + ",";
            }

            // add a_data
            merged_data += split_a[i] + ",";
            ctr ++;

            // concatenate 3 g_data every 3rd a_data
            if (ctr == 3){
                ctr = 0;

                for (int j = start; j <= end; j++){
                    if (j == end){
                        merged_data += split_g[j] + "\n"; // newline without ',' every third g_data
                    }else{
                        merged_data += split_g[j] + ",";
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
        if (a_data.split(",").length == g_data.split(",").length ||
                a_data.split(",").length < g_data.split(",").length){

            data_len = a_data.split(",").length;
        }else{

            data_len = g_data.split(",").length;
        }

        return data_len;
    }
}
