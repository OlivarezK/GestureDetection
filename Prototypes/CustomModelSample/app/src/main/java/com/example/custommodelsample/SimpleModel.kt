package com.example.custommodelsample

import android.app.Activity
import android.os.Bundle
import com.example.custommodelsample.databinding.ActivityMainBinding
import com.example.custommodelsample.ml.Model
import com.google.android.gms.tflite.java.TfLite
import org.tensorflow.lite.DataType
import org.tensorflow.lite.InterpreterApi
import org.tensorflow.lite.InterpreterApi.Options.TfLiteRuntime
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.nio.ByteBuffer

class SimpleModel : Activity() {
    //val initializeTask: Task<Void> by lazy { TfLite.initialize(this) }

    private lateinit var binding: ActivityMainBinding
    private lateinit var byteBuffer: ByteBuffer;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.submit.setOnClickListener{
//            val input : Float = binding.input.text.toString().toFloat();
//            val prediction = Predict(input)
//            binding.text.text = prediction.toString();
//        }
    }

    fun Predict(value: Float) : Float{
        val model = Model.newInstance(this)
        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1), DataType.FLOAT32)
        val inputArray = FloatArray(1);
        inputArray[0] = value
        inputFeature0.loadArray(inputArray);

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val predictedValue = outputFeature0.getFloatValue(0)

        // Releases model resources if no longer used.
        model.close()

        return predictedValue
    }
}