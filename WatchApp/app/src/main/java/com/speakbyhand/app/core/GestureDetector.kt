package com.speakbyhand.app.core

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel


class GestureDetector(var context: Context) {

    fun detect(data: GestureData): GestureCode {
        Log.i("Data Count: ", data.count.toString())
        val interpreter = Interpreter(readModelFile())
        val input = TensorBuffer.createFixedSize(intArrayOf(1, 297, 6), DataType.FLOAT32)
        val output = TensorBuffer.createFixedSize(intArrayOf(1, 6), DataType.FLOAT32)
        println(data.count)
        input.loadArray(data.toArray())
        interpreter.run(input.buffer, output.buffer);

        if(arePredictionsAboveConfidenceLevel(output.floatArray)){
            val predictionIndex = getArgMax(output.floatArray)
            Log.i("result", output.floatArray[predictionIndex].toString())

            return toGestureCode(predictionIndex)
        } else {
            return GestureCode.Unknown;
        }
    }

    fun readModelFile() : ByteBuffer {
        val fileDescriptor: AssetFileDescriptor = context.assets.openFd("gesture_conv_model_n.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.getChannel()
        val startOffset = fileDescriptor.startOffset
        val declareLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declareLength)
    }

    private fun arePredictionsAboveConfidenceLevel(floatArray: FloatArray) : Boolean{
        for (i in floatArray.indices) {
            if (floatArray[i] > .6f) {
                return true;
            }
        }

        return false;
    }

    // TODO: Create unit test for this method
    private fun getArgMax(floatArray: FloatArray): Int {
        var maxIndex = 0
        for (i in floatArray.indices) {
            if (floatArray[i] > floatArray[maxIndex]) {
                maxIndex = i
            }
        }
        return maxIndex
    }

    // TODO: Create unit test for this method
    private fun toGestureCode(predictionIndex: Int): GestureCode {
        when (predictionIndex) {
            0 -> return GestureCode.DrinkWater
            1 -> return GestureCode.EatFood
            2 -> return GestureCode.Help
            3 -> return GestureCode.No
            4 -> return GestureCode.Toilet
            5 -> return GestureCode.Yes
//            6 -> return GestureCode.Unknown
        }
        throw IllegalArgumentException("Given argument: $predictionIndex")
    }
}