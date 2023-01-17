/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.benchmarkapp.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.benchmarkapp.presentation.core.GestureDataReader
import com.example.benchmarkapp.presentation.core.GestureDetector
import com.example.benchmarkapp.presentation.core.TimeRecorder
import com.example.benchmarkapp.presentation.core.client.BenchmarkApiService
import com.example.benchmarkapp.presentation.core.client.BenchmarkResult
import com.example.benchmarkapp.presentation.theme.BenchmarkAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp { startBenchmark(this) }
        }
    }

    private fun startBenchmark(context: Context) {
        val gestureDataReader = GestureDataReader()
        val timeRecorder = TimeRecorder()
        val apiService = BenchmarkApiService()

        val modelNames = arrayOf("Convolutional Model", "Recurrrent Model")
        val modelFilePaths = arrayOf("gesture_conv_model_n.tflite", "gesture_recurr_model_n.tflite")
        val dataFileNames = arrayOf("Eat_1_n_n", "Drink_1_n_n", "Help_1_n_n", "No_1_n_n", "Toilet_1_n_n", "Yes_1_n_n")

        val benchmarkResults = modelNames.zip(modelFilePaths).map { (modelName, modelPath) ->
            val gestureDetector = GestureDetector(context, modelPath)
            val benchmarkResult = BenchmarkResult(modelName)
            for (filename in dataFileNames) {
                // read data
                gestureDataReader.readGestureData(context, filename)
                val gestureData = gestureDataReader.data

                // feed data and run model
                timeRecorder.startTimer()
                val detection = gestureDetector.detect(gestureData)
                val inferenceTime = timeRecorder.stopTimer()

                // prediction
                Log.i("File Name", filename)
                Log.i("Inference Time", inferenceTime.toString())
                Log.i("Prediction", detection.toString())
                benchmarkResult.add(filename, inferenceTime, detection)

                gestureDataReader.reset()
            }
            benchmarkResult
        }

        apiService.postData(benchmarkResults)
        Toast.makeText(context, "Benchmark Complete", Toast.LENGTH_SHORT).show()

    }
}

@Composable
fun WearApp(onStart: () -> Unit) {
    val IDLE_STATE = "IDLE"
    val RUNNING_MODEL_STATE = "RUNNING"
    val currentState = remember { mutableStateOf(IDLE_STATE) }

    var prompt: String by remember { mutableStateOf(IDLE_STATE) }

    BenchmarkAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                enabled = currentState.value == IDLE_STATE,
                onClick = {
                    currentState.value = RUNNING_MODEL_STATE
                    prompt = RUNNING_MODEL_STATE
                    onStart()
                    currentState.value = IDLE_STATE
                    prompt = IDLE_STATE
                }
            ) {
                Text(text = "Start")
            }
            Text(
                text = prompt
            )
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp({})
}