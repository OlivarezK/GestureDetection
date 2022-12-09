/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.speakbyhand.datacollectorclient.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.speakbyhand.datacollectorclient.core.GestureDataApiService
import com.speakbyhand.datacollectorclient.core.GestureDataRecorder
import com.speakbyhand.datacollectorclient.presentation.theme.DataCollectorClientTheme


class MainActivity : ComponentActivity() {
    lateinit var apiService: GestureDataApiService
    lateinit var dataRecorder: GestureDataRecorder


    override fun onCreate(savedInstanceState: Bundle?) {
        apiService = GestureDataApiService()
        dataRecorder = GestureDataRecorder()
        dataRecorder.initializeGestureComponents(this)

        super.onCreate(savedInstanceState)
        setContent {
            WearApp({ startRecording() }, { stopRecording() })
        }
    }

    fun startRecording() {
        dataRecorder.recordGesture()
    }

    fun stopRecording() {
        dataRecorder.unregisterListeners()
        val mergedData: String = dataRecorder.mergeToCsv()
        Log.i("merged data", mergedData)
        apiService.postData(mergedData)
        dataRecorder.clearData()
    }
}


@Composable
fun WearApp(onStart: () -> Unit, onStop: () -> Unit) {
    val IDLE_STATE = "IDLE"
    val RECORDING_STATE = "RECORDING"
    val currentState = remember { mutableStateOf(IDLE_STATE) }
    DataCollectorClientTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Start Button
            Button(
                enabled = currentState.value == IDLE_STATE,
                onClick = {
                    onStart()
                    currentState.value = RECORDING_STATE
                },
            ) {
                Text(text = "Start")
            }
            // Stop Button
            Button(
                enabled = currentState.value == RECORDING_STATE,
                onClick = {
                    onStop()
                    currentState.value = IDLE_STATE
                },
            ) {
                Text(text = "Stop")
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp({}, {})
}