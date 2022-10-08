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

import android.support.v4.app.RemoteActionCompatParcelizer
import android.widget.Button
import android.widget.Toast
import com.example.custommodelsample.ml.GestureModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class MainActivity : Activity() {
    //val initializeTask: Task<Void> by lazy { TfLite.initialize(this) }

    private lateinit var binding: ActivityMainBinding
    //private lateinit var interpreter: InterpreterApi
    private lateinit var byteBuffer: ByteBuffer;
    private val input = FloatArray(127*3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        /*
        val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        sensorManager.registerListener(GyroscopeListener(fun(x: Float,y:Float,z:Float){
            binding.gyroscopeText.text = "Gyroscope Readings $x $y $x"
        }), gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
         */


        binding.btnStart.setOnClickListener{
            sensorManager.registerListener(AccelerometerListener(fun(x: Float,y:Float,z:Float){
                for (i in 0..380 step 3){
                    input[i] = x
                    input[i+1] = y
                    input[i+2] = z
                }
                //binding.accelerometerText.text = "Accelerometer Readings $x $y $x"
            }), accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        binding.btnStop.setOnClickListener {
            sensorManager.unregisterListener(AccelerometerListener(fun(x: Float,y:Float,z:Float){}))

            val prediction = Predict(input)

            if(prediction[0] > prediction[1]){
                binding.txtPrediction.text = "Prediction: Right"
            }else if(prediction[0] < prediction[1]){
                binding.txtPrediction.text = "Prediction: Left"
            }else{
                binding.txtPrediction.text = "Prediction: Neither"
            }
        }
    }

    fun Predict(value: FloatArray) : FloatArray{
        val model = GestureModel.newInstance(this)
        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 127, 3), DataType.FLOAT32)
        //inputArray = value
        inputFeature0.loadArray(value)
        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val predictedValue = floatArrayOf(outputFeature0.getFloatValue(0), outputFeature0.getFloatValue(1))

        // Releases model resources if no longer used.
        model.close()
        return predictedValue
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
    var currentState: DetectorState = DetectorState.Idle;

    fun Idle(){

    }

    fun Recording(){
        currentState = DetectorState.Recording;
        var recordingState = RecordingState()

    }

    fun Detecting(){
        currentState = DetectorState.Detecting;
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