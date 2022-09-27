package com.example.custommodelsample

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import com.example.custommodelsample.databinding.ActivityMainBinding
import java.nio.ByteBuffer

class MainActivity : Activity() {
    //val initializeTask: Task<Void> by lazy { TfLite.initialize(this) }

    private lateinit var binding: ActivityMainBinding
    //private lateinit var interpreter: InterpreterApi
    private lateinit var byteBuffer: ByteBuffer;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(AccelerometerListener(fun(x: Float,y:Float,z:Float){
            binding.accelerometerText.text = "Accelerometer Readings $x $y $x"
        }), accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        sensorManager.registerListener(GyroscopeListener(fun(x: Float,y:Float,z:Float){
            binding.gyroscopeText.text = "Gyroscope Readings $x $y $x"
        }), gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private val sensorEventListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        override fun onSensorChanged(event: SensorEvent) {

        }
    }
}


enum class DetectorState {
    Idle,
    Recording,
    Detecting,
}

private class Detector{
    val currentState: DetectorState = DetectorState.Idle;

    fun Idle(){

    }

    fun Recording(){

    }

    fun Detecting(){

    }
}

private class RecordingState{
    fun StartRecording(){

    }

    fun StopRecording(){

    }
}

private class DetectingState(){
    fun Detect(){

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