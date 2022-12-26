/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.speakbyhand.datacollectorclient.presentation

import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import java.util.*


class MainActivity : ComponentActivity() {
    lateinit var apiService: GestureDataApiService
    lateinit var dataRecorder: GestureDataRecorder

    lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        apiService = GestureDataApiService()
        dataRecorder = GestureDataRecorder(this)

        super.onCreate(savedInstanceState)
        setContent {
            WearApp({ startRecording() }, { stopRecording() })
        }
    }

    fun startRecording() {
        dataRecorder.start()
    }

    fun stopRecording() {
        dataRecorder.stop()
        playSpeech()

        val mergedData: String = dataRecorder.getDataAsCsvString()
        Log.i("merged data", mergedData)

        apiService.postData(mergedData)
        dataRecorder.reset()
    }

    fun playSpeech() {
        tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
            if(it == TextToSpeech.SUCCESS){
                tts.language = Locale.US
                tts.setSpeechRate(1.0f)
                tts.speak("Recording Successful", TextToSpeech.QUEUE_ADD, null)
            }
        })
    }
}


@Composable
fun WearApp(onStart: () -> Unit, onStop: () -> Unit) {
    val IDLE_STATE = "IDLE"
    val RECORDING_STATE = "RECORDING"
    val currentState = remember { mutableStateOf(IDLE_STATE) }

    var nums :Long by remember{ mutableStateOf(2) }
    var setView: String by remember{ mutableStateOf("Click to Start...") }
    val cuntNum = object :CountDownTimer(2000, 1000){
        override fun onTick(millisUntilFinished: Long) {
            nums  = millisUntilFinished/1000
            setView = "$nums"
        }
        override fun onFinish() {
            setView = "Click to Start..."
            onStop()
            currentState.value = IDLE_STATE
        }
    }

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
                    cuntNum.start()
                    currentState.value = RECORDING_STATE
                },
            ) {
                Text(text = "Start")
            }
            Text(
                text = "$setView"
            )
            // Stop Button
            /*Button(
                enabled = currentState.value == RECORDING_STATE,
                onClick = {
                    onStop()
                    currentState.value = IDLE_STATE
                },
            ) {
                Text(text = "Stop")
            }*/
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp({}, {})
}