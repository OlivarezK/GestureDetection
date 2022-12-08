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
import java.time.ZonedDateTime;
import java.util.Date;

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

    // UI
    private Button btnStart, btnStop;

    // for API
    private final OkHttpClient client = new OkHttpClient();
    private int fileNum = 1;

    // for gesture detection
    private GestureDetection gesture = new GestureDetection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // initialize components
        initializeComponents(this);

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
    }

    private void initializeComponents(Context onCreateContext){

        // initialize components
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        gesture.initializeGestureComponents(onCreateContext);
    }

    private void startRecording(){
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);

        // record accelerometer data
        gesture.recordGesture();
    }

    private void stopRecording(){
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);

        // stop recording
        gesture.unregisterListeners();

        // merge time_data, a_data and g_data
        String mergedData = gesture.mergeToCsv();
        Log.i("merged data", mergedData);

        // post data to API
        postData(mergedData);

        gesture.clearData();
    }

    private void postData(String data){
        // API
        RequestBody formBody = new FormBody.Builder()
                .add("value", data)
                .add("fileNum", String.valueOf(fileNum))
                .build();
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

                    fileNum += 1;
                }
            }
        });
    }
}