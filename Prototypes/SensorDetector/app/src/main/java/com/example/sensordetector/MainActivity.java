package com.example.sensordetector;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Arrays;
import java.util.Date;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView txt_xAxis;
    private Button btnStart, btnStop;

    private double[][] gesture_matrix = new double[100][];
    private int matrix_idx = 0;

    private SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            double[] values = {sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2]};

            if (matrix_idx < 100){
                gesture_matrix[matrix_idx] = values;
                matrix_idx += 1;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_xAxis = (TextView) findViewById(R.id.xAxis);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStop = (Button) findViewById(R.id.btn_stop);

        Log.d(TAG, "onCreate: Initializing Sensor Services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGesture();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endGesture();
            }
        });

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Log.d(TAG, "onCreate: Registered Accelerometer listener");
    }

    private void startGesture() {
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);

        sensorManager.registerListener(mSensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void endGesture() {
        sensorManager.unregisterListener(mSensorListener);
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);

        txt_xAxis.setText(Arrays.deepToString(gesture_matrix));
        Log.d(TAG, "Gesture Matrix Data: " + Arrays.deepToString(gesture_matrix));
    }

}