package com.example.custommodelsample

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.example.custommodelsample.databinding.ActivityMainBinding
import com.example.custommodelsample.ml.Model
import com.google.android.gms.tasks.Task
import com.google.android.gms.tflite.java.TfLite
import org.tensorflow.lite.DataType
import org.tensorflow.lite.InterpreterApi
import org.tensorflow.lite.InterpreterApi.Options.TfLiteRuntime
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File

class MainActivity : Activity() {
    val initializeTask: Task<Void> by lazy { TfLite.initialize(this) }

    private lateinit var binding: ActivityMainBinding
    private lateinit var interpreter: InterpreterApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        val model = Model.newInstance(this)

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1), DataType.FLOAT32)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        // Releases model resources if no longer used.
        model.close()

         */

        val modelfile = File("/ml/model.tflite")

        initializeTask.addOnSuccessListener {
            val interpreterOption = InterpreterApi.Options().setRuntime(TfLiteRuntime.FROM_SYSTEM_ONLY)
            interpreter = InterpreterApi.create(modelfile, interpreterOption)
        }.addOnFailureListener { e -> Log.e("Interpreter", "Cannot initialize interpreter", e) }

        //interpreter.run(inputFeature0, outputFeature0)
    }
}