package com.example.custommodelsample

import android.app.Activity
import android.os.Bundle
import com.example.custommodelsample.databinding.ActivityMainBinding
import com.example.custommodelsample.ml.GestureModel
import com.google.android.gms.tflite.java.TfLite
import org.tensorflow.lite.DataType
import org.tensorflow.lite.InterpreterApi
import org.tensorflow.lite.InterpreterApi.Options.TfLiteRuntime
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.nio.ByteBuffer

class GestureModel: Activity() {
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

    fun Predict(value: FloatArray) : FloatArray{
        val model = GestureModel.newInstance(this)

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 127, 3), DataType.FLOAT32)
        //inputArray = value
        inputFeature0.loadArray(value);

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val predictedValue = outputFeature0.floatArray

        // Releases model resources if no longer used.
        model.close()

        return predictedValue
    }
}