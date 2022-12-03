package com.example.datacollectiontool;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.datacollectiontool.databinding.ActivityMainBinding;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends Activity {

    private TextView mTextView;
    private ActivityMainBinding binding;

    private Button btnStart, btnStop;

    // for gesture detection
    private String a_data = "", g_data = "";
    private float a_x, a_y, a_z, g_x, g_y, g_z;

    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope;

    private SensorEventListener accelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            a_x = sensorEvent.values[0];
            a_y = sensorEvent.values[1];
            a_z = sensorEvent.values[2];

            a_data += String.valueOf(a_x) + "," + String.valueOf(a_y) + "," + String.valueOf(a_z) + ",";
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };
    private SensorEventListener gyroscopeListener = new SensorEventListener() {
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

    // for bluetooth connectivity
    // TODO: Implement bluetooth

    // for API
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // initialize components
        initializeComponents();

        // start button onclick
        btnStop.setEnabled(false);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecording();
            }
        });

        // stop button onclick
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
            }
        });

        // API
        RequestBody formBody = new FormBody.Builder().add("value", "sample post string.").build();
        Request request = new Request.Builder()
                .url("http:/192.168.100.7:5000/post")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    ResponseBody responseBody = response.body();
                    Log.d(TAG, "onResponse: " + responseBody.string());
                }
            }
        });
    }

    private void initializeComponents(){
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);

        // sensor components
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    private void startRecording(){
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);

        // record accelerometer data
        sensorManager.registerListener(accelerometerListener, accelerometer, sensorManager.SENSOR_DELAY_NORMAL);

        // record gyroscope data
        sensorManager.registerListener(gyroscopeListener, gyroscope, sensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopRecording(){
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);

        // stop recording
        sensorManager.unregisterListener(accelerometerListener);
        sensorManager.unregisterListener(gyroscopeListener);

        Log.i("accelerometer data", a_data);
        Log.i("gyroscope data", g_data);

        // merge a_data and g_data
        String mergedData = mergeData(a_data, g_data);

        Log.i("merged data", mergedData);

        // clear a_data and g_data
        a_data = "";
        g_data = "";
    }

    private String mergeData(String a_data, String g_data){
        String merged_data = "";
        int dataLen = 0;

        String[] split_a = a_data.split(",");
        String[] split_g = g_data.split(",");

        // check if accelerometer and gyro data have same no. of data
        // if not, get smaller size
        if (split_a.length != split_g.length){
            if (split_a.length < split_g.length){
                dataLen = split_a.length;
            }else{
                dataLen = split_g.length;
            }
        }

        int ctr = 0;
        int start = 0;
        int end = 2;

        for (int i = 0; i < dataLen; i++){
            ctr ++;
            merged_data += split_a[i] + ",";

            if (ctr == 3){ // concatenate 3 g_data every 3rd a_data
                ctr = 0;

                for (int j = start; j <= end; j++){
                    if (j == end){
                        merged_data += split_g[j] + "\n";
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
}