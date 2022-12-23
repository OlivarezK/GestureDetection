/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.speakbyhand.app.presentation

import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import com.speakbyhand.app.R
import com.speakbyhand.app.core.*
import com.speakbyhand.app.presentation.theme.SpeakByHandTheme
import kotlin.concurrent.thread
import kotlin.math.roundToInt


enum class AppState {
    WaitingDelimiter,
    PerformingGesture,
    SpeakingPhrase,
    UnknownGesture
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Logic
        super.onCreate(savedInstanceState)

        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val delimiterDetector = NewDelimiterDetector()
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        val textToSpeech = TextToSpeech(applicationContext) {}
        val gestureDataRecorder = GestureDataRecorder()
        val gestureDetector = GestureDetector()

        // UI
        setContent {
            var currentState by rememberSaveable { mutableStateOf(AppState.WaitingDelimiter) }
            SpeakByHandTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background),
                    verticalArrangement = Arrangement.Center
                ) {
                    when (currentState) {
                        AppState.WaitingDelimiter -> WaitingDelimiter(
                            onStart = {
                                delimiterDetector.start(sensorManager)
                            },
                            detectDelimiter = {
                                delimiterDetector.isDelimiterDetected
                            },
                            onDelimiterDetected = {
                                currentState = AppState.PerformingGesture
                            },
                            onFinish = {
                                delimiterDetector.stop()
                            }
                        )
                        AppState.PerformingGesture -> PerformingGesture(
                            onStart = {
                                gestureDataRecorder.start()
                            },
                            detectGestureCode = {
                                val gestureData = gestureDataRecorder.gestureData
                                val detection = gestureDetector.detect(gestureData)
                                Pair(detection != null, detection)
                            },
                            onGestureDetected = {
                                currentState = AppState.SpeakingPhrase
                            },
                            onGestureNotDetected = {
                                currentState = AppState.UnknownGesture
                            },
                            onFinish = {
                                gestureDataRecorder.reset()
                                gestureDataRecorder.stop()
                            }
                        )
                        AppState.SpeakingPhrase -> SpeakingPhrase(
                            textToSpeech = textToSpeech,
                            onFinish = {
                                currentState = AppState.WaitingDelimiter
                            }
                        )
                        AppState.UnknownGesture -> UnknownGesture(
                            vibrator = vibrator,
                            onFinish = {
                                currentState = AppState.WaitingDelimiter
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun WaitingDelimiter(
    onStart: () -> Unit,
    detectDelimiter: () -> Boolean,
    onDelimiterDetected: () -> Unit,
    onFinish: () -> Unit
) {
    // Logic
    onStart()
    thread {
        // Disabled for testing
/*      val isDelimiterDetected = detectDelimiter()
        while (!isDelimiterDetected){
            Thread.sleep(500)
        }*/
        detectDelimiter()
        Thread.sleep(5000)
        onDelimiterDetected()
        onFinish()
    }

    // UI
    ShowText(stringResource(R.string.shake), fontSize = 30.sp)

}

@Composable
fun PerformingGesture(
    onStart: () -> Unit,
    detectGestureCode: () -> Pair<Boolean, GestureCode>,
    onGestureDetected: (GestureCode) -> Unit,
    onGestureNotDetected: () -> Unit,
    onFinish: () -> Unit
) {
    // Logic
    onStart()
    object : CountDownTimer(2000, 50) {
        var hasDetectedGesture = false
        var detectedGestureCode: GestureCode? = null
        override fun onTick(millisUntilFinished: Long) {
            val detection = detectGestureCode()
            hasDetectedGesture = detection.first
            detectedGestureCode = detection.second
        }

        override fun onFinish() {
            if (hasDetectedGesture) {
                onGestureDetected(detectedGestureCode!!)
            } else {
                onGestureNotDetected()
            }
            onFinish()
        }
    }.start()

    // UI
    Timer(
        totalTime = 2L * 1000L,
        inactiveBarColor = Color.DarkGray,
        activeBarColor = MaterialTheme.colors.primary,
        modifier = Modifier.size(200.dp)
    )
}

@Composable
fun SpeakingPhrase(textToSpeech: TextToSpeech, onFinish: () -> Unit) {
    // Logic
    textToSpeech.speak("SPEAK SPEAK SPEAK", TextToSpeech.QUEUE_FLUSH, null, null)
    thread {
        do {
            val speakingEnd = textToSpeech.isSpeaking
        } while (speakingEnd)
        onFinish()
    }

    // UI
    ShowImage()
}

@Composable
fun UnknownGesture(vibrator: Vibrator, onFinish: () -> Unit) {
    // Logic
    val mVibratePattern = longArrayOf(0, 500, 500, 500)
    val effect = VibrationEffect.createWaveform(mVibratePattern, -1)
    vibrator.vibrate(effect)
    object : CountDownTimer(3000, 500) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            onFinish()
        }
    }.start()

    // UI
    ShowText(stringResource(R.string.unknown), fontSize = 25.sp)
    ShowText(stringResource(R.string.question_mark), fontSize = 40.sp)
}


@Composable
fun Timer(
    totalTime: Long, inactiveBarColor: Color, activeBarColor: Color, modifier: Modifier = Modifier,
    initialValue: Float = 1f, strokeWidth: Dp = 20.dp
) {
    // Logic
    var size by remember { mutableStateOf(IntSize.Zero) }
    var value by remember { mutableStateOf(initialValue) }
    var currentTime by remember { mutableStateOf(totalTime) }
    LaunchedEffect(key1 = currentTime) {
        if (currentTime > 0) {
            currentTime -= 50L
            value = currentTime / totalTime.toFloat()
        }
    }

    // UI
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .onSizeChanged {
                size = it
            }
    ) {
        Canvas(modifier = modifier) {
            drawArc(
                color = inactiveBarColor,
                startAngle = -215f,
                sweepAngle = 250f,
                useCenter = false,
                size = Size(size.width.toFloat(), size.height.toFloat()),
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = activeBarColor,
                startAngle = -215f,
                sweepAngle = 250f * value,
                useCenter = false,
                size = Size(size.width.toFloat(), size.height.toFloat()),
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        Column() {
            val countdown = currentTime.toDouble() / 1000
            ShowText(stringResource(R.string.perform), fontSize = 20.sp)
            ShowText(
                text = String.format("%.1f", (countdown * 2).roundToInt() / 2.0),
                fontSize = 40.sp
            )
        }
    }
}