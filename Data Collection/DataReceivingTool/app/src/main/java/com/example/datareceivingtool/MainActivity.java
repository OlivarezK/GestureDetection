package com.example.datareceivingtool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button btnList, btnWrite;
    ListView listDevices;
    TextView txtData;

    // bluetooth components
    BluetoothAdapter btAdapter;
    BluetoothDevice[] btArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();

        showDevices();
    }

    private void showDevices() {
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Set<BluetoothDevice> bt = btAdapter.getBondedDevices();
                    String[] devNames = new String[bt.size()];
                    int idx = 0;

                    if (bt.size() > 0){
                        for (BluetoothDevice device: bt){
                            btArray[idx] = device; // save devices to array
                            devNames[idx] = device.getName(); // save name of devices
                            idx++;
                        }

                        // display device names in list view
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, devNames);
                        listDevices.setAdapter(arrayAdapter);
                    }
                }catch (SecurityException e){
                    Log.i("Error", "Bluetooth Adapter Error");
                }
            }
        });
    }

    private void initializeComponents() {
        btnList = (Button) findViewById(R.id.btnList);
        btnWrite = (Button) findViewById(R.id.btnWrite);
        listDevices = (ListView) findViewById(R.id.listDevices);
        txtData = (TextView) findViewById(R.id.txtData);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }
}