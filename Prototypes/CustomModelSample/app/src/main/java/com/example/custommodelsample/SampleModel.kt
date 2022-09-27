package com.example.custommodelsample

import android.content.Context
import com.google.android.gms.tflite.java.TfLite
import org.tensorflow.lite.DataType
import org.tensorflow.lite.InterpreterApi
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File

class SampleModel (context: Context) {

    init {
        TfLite.initialize(context)
    }

    fun predict(x:Float): Float{
        val modelFile = File("model.tflite")
        val options = InterpreterApi.Options().setRuntime(InterpreterApi.Options.TfLiteRuntime.FROM_SYSTEM_ONLY)
        val interpreterApi = InterpreterApi.create(modelFile, options)

        val inputShape: IntArray = interpreterApi.getInputTensor( 0).shape()
        val inputTensor = TensorBuffer.createFixedSize(inputShape, DataType.FLOAT32)
        val inputArray = FloatArray(1);
        inputArray[0] = 1f
        inputTensor.loadArray(inputArray)

        val outputShape: IntArray = interpreterApi.getOutputTensor(0).shape()
        val outputTensor = TensorBuffer.createFixedSize(outputShape, DataType.FLOAT32)
        val outputArray = FloatArray(1);
        outputTensor.loadArray(outputArray);

        interpreterApi.run(inputTensor, outputTensor);

        outputTensor.getFloatValue(0);
        return 0f;
    }

}