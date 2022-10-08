package com.example.pollingratechecker

import com.example.pollingratechecker.databinding.ActivityMainBinding
import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import java.nio.ByteBuffer

class MainActivity : Activity() {
    //val initializeTask: Task<Void> by lazy { TfLite.initialize(this) }

    private lateinit var binding: ActivityMainBinding
    //private lateinit var interpreter: InterpreterApi
    private lateinit var byteBuffer: ByteBuffer;
    private var isChecking = false;
    private var currentTime = 0;
    private var currentAccelerometerCount = 0;
    private var currentGyroscopeCount = 0;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(AccelerometerListener(fun(x: Float,y:Float,z:Float){
            if(isChecking){
                currentAccelerometerCount += 1;
            }
        }), accelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        sensorManager.registerListener(GyroscopeListener(fun(x: Float,y:Float,z:Float){
            if(isChecking){
                currentGyroscopeCount += 1;
            }
        }), gyroscope, SensorManager.SENSOR_DELAY_FASTEST);

        val timer = object: CountDownTimer(5 * 1000, 1 + 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                isChecking = false;
                binding.gyroscopeText.text = currentGyroscopeCount.toString();
                binding.accelerometerText.text = currentAccelerometerCount.toString();
            }
        }
        timer.start()
        isChecking = true;
    }

    private val sensorEventListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        override fun onSensorChanged(event: SensorEvent) {

        }
    }
}



private class AccelerometerListener(val callback: (Float, Float, Float) -> Unit)  : SensorEventListener{

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0];
        val y = event.values[1];
        val z = event.values[2];

        callback(x,y,z)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}

private class GyroscopeListener(val callback: (Float, Float, Float) -> Unit) : SensorEventListener{
    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0];
        val y = event.values[1];
        val z = event.values[2];

        callback(x,y,z)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}