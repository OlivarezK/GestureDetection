package com.example.benchmarkapp.presentation.core;

import static java.security.AccessController.getContext;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class GestureDataReader {
    private GestureData gestureData = new GestureData();

    public void readGestureData(Context context){
        try {
            InputStream is = context.getAssets().open("Drink_1");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );

            String line;
            while ((line = reader.readLine()) != null){
                String[] data = line.split(",");

                gestureData.addTime(Float.parseFloat(data[0]));
                gestureData.addAccelerometerData(Float.parseFloat(data[1]), Float.parseFloat(data[2]), Float.parseFloat(data[3]));
                gestureData.addGyroscopeData(Float.parseFloat(data[4]), Float.parseFloat(data[5]), Float.parseFloat(data[6]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GestureData getData() {return gestureData;}

}
