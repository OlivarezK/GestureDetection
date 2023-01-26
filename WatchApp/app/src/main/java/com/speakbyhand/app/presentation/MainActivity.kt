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
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.speakbyhand.app.R
import com.speakbyhand.app.core.*
import com.speakbyhand.app.presentation.theme.SpeakByHandTheme
import kotlinx.coroutines.delay
import kotlin.concurrent.thread
import kotlin.math.roundToInt


enum class AppState {
    WaitingDelimiter,
    PerformingGesture,
    SpeakingPhrase,
    UnknownGesture,
    ButtonMode
}

enum class AppMode{
    ShakeMode,
    ButtonMode
}

class MainActivity : ComponentActivity() {
    private val swipeDetector = SwipeDetector()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val delimiterDetector = DelimiterDetector()
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        val textToSpeech = TextToSpeech(applicationContext) {}
        val gestureDataRecorder = GestureDataRecorder(this)
        val gestureDetector = GestureDetector(this, "gesture_conv_model_n.tflite")

        setContent {
            var currentState by rememberSaveable { mutableStateOf(AppState.WaitingDelimiter) }
            var mode by rememberSaveable { mutableStateOf(AppMode.ShakeMode) }
            var detectedGestureCode by rememberSaveable { mutableStateOf(GestureCode.Sample) }

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
                            },
                            detectSwipe = {
                                swipeDetector.isSwiped
                            },
                            onModeChanged = {
                                swipeDetector.isSwiped = false
                                currentState = AppState.ButtonMode
                                mode = AppMode.ButtonMode
                            },
                            vibrator = vibrator
                        )
                        AppState.ButtonMode -> ButtonMode(
                            onStart = {
                                currentState = AppState.PerformingGesture
                            },
                            detectSwipe = {
                                swipeDetector.isSwiped
                            },
                            onModeChanged = {
                                swipeDetector.isSwiped = false
                                currentState = AppState.WaitingDelimiter
                                mode = AppMode.ShakeMode
                            },
                            vibrator = vibrator
                        )
                        AppState.PerformingGesture -> PerformingGesture(
                            onStart = {
                                gestureDataRecorder.start()
                            },
                            detectGestureCode = {
                                val gestureData = gestureDataRecorder.data
                                val detection = gestureDetector.detect(gestureData)
                                detectedGestureCode = detection
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
                            },
                            vibrator = vibrator,
                            detectPause = {
                                gestureDataRecorder.isPaused
                            }
                        )
                        AppState.SpeakingPhrase -> SpeakingPhrase(
                            textToSpeech = textToSpeech,
                            phrase = MappedPhrases.fromGestureCode(detectedGestureCode),
                            gesture = detectedGestureCode,
                            onFinish = {
                                currentState = when(mode){
                                    AppMode.ShakeMode -> AppState.WaitingDelimiter
                                    AppMode.ButtonMode -> AppState.ButtonMode
                                }
                            }
                        )
                        AppState.UnknownGesture -> UnknownGesture(
                            onFinish = {
                                currentState = when(mode){
                                    AppMode.ShakeMode -> AppState.WaitingDelimiter
                                    AppMode.ButtonMode -> AppState.ButtonMode
                                }
                            },
                            textToSpeech = textToSpeech
                        )
                    }
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (swipeDetector.gestureDetector.onTouchEvent(event)) {
            true
        }
        else {
            super.onTouchEvent(event)
        }
    }
}

@Composable
fun WaitingDelimiter(
    onStart: () -> Unit,
    detectDelimiter: () -> Boolean,
    onDelimiterDetected: () -> Unit,
    onFinish: () -> Unit,
    detectSwipe: () -> Boolean,
    onModeChanged: () -> Unit,
    vibrator: Vibrator
) {
    // Logic
    onStart()
    thread {
        do {
            val isDelimiterDetected = detectDelimiter()
            val isSwitched = detectSwipe()
        } while (!isDelimiterDetected && !isSwitched)

        if (detectSwipe()){
            onModeChanged()
        }else{
            val effect = VibrationEffect.createOneShot(500, -1)
            vibrator.vibrate(effect)

            val startTime = System.currentTimeMillis()
            do{
                val currentTime = System.currentTimeMillis()
            }while (currentTime - startTime < 500)

            onDelimiterDetected()
        }

        onFinish()
    }

    // UI
    ShowText(stringResource(R.string.shake), fontSize = 30.sp)

}

@Composable
fun ButtonMode(
    onStart: () -> Unit,
    detectSwipe: () -> Boolean,
    onModeChanged: () -> Unit,
    vibrator: Vibrator
){
    //Logic
    thread{
        do {
            val isSwitched = detectSwipe()
        } while (!isSwitched)

        onModeChanged()
    }

    //UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            onStart()

            val effect = VibrationEffect.createOneShot(500, -1)
            vibrator.vibrate(effect)
        },
        modifier = Modifier.size(120.dp)) {
            Text(text = "START", fontSize = 20.sp)
        }
    }
}

@Composable
fun PerformingGesture(
    onStart: () -> Unit,
    detectGestureCode: () -> Pair<Boolean, GestureCode>,
    onGestureDetected: (GestureCode) -> Unit,
    onGestureNotDetected: () -> Unit,
    onFinish: () -> Unit,
    vibrator: Vibrator,
    detectPause: () -> Boolean
) {
    // Logic
    onStart()
    var moved = false

    object : CountDownTimer(3000, 10) {
        override fun onTick(millisUntilFinished: Long) {
            val notMoving = detectPause()

            if(!notMoving){
                moved = true
            }
        }

        override fun onFinish() {
            if (moved){
                val detection = detectGestureCode()
                val hasDetectedGesture = detection.first
                val detectedGestureCode = detection.second

                if (hasDetectedGesture) {
                    onGestureDetected(detectedGestureCode)
                } else {
                    onGestureNotDetected()
                }
            }else{
                onGestureNotDetected()
            }

            val effect = VibrationEffect.createOneShot(500, -1)
            vibrator.vibrate(effect)

            onFinish()
        }
    }.start()

    // UI
    Timer(
        totalTime = 3L * 1000L,
        inactiveBarColor = Color.DarkGray,
        activeBarColor = MaterialTheme.colors.primary,
        modifier = Modifier.size(200.dp)
    )
}

@Composable
fun SpeakingPhrase(textToSpeech: TextToSpeech, phrase: String, onFinish: () -> Unit, gesture: GestureCode) {
    // Logic
    textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener(){
        override fun onStart(p0: String?) {
        }

        override fun onDone(p0: String?) {
            onFinish()
        }

        @Deprecated("On error is Deprecated")
        override fun onError(p0: String?) {
        }
    })
    textToSpeech.speak(phrase, TextToSpeech.QUEUE_FLUSH, null, "123")

    // UI
    GestureDisplay(gesture)
}

@Composable
fun GestureDisplay(gesture: GestureCode){
    Image(
        painterResource(
            when(gesture){
                GestureCode.Yes -> R.drawable.yes
                GestureCode.Help -> R.drawable.help
                GestureCode.DrinkWater -> R.drawable.drink
                GestureCode.EatFood -> R.drawable.eat
                GestureCode.No -> R.drawable.no
                GestureCode.Toilet -> R.drawable.toilet
                else -> {R.drawable.unknown}
            }
        ),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .requiredSize(100.dp),
        alignment = Alignment.Center
    )
}

@Composable
fun UnknownGesture(onFinish: () -> Unit, textToSpeech: TextToSpeech) {
    // Logic
    textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener(){
        override fun onStart(p0: String?) {
        }

        override fun onDone(p0: String?) {
            onFinish()
        }

        @Deprecated("On error is Deprecated")
        override fun onError(p0: String?) {
        }
    })
    textToSpeech.speak("Gesture not recognized", TextToSpeech.QUEUE_FLUSH, null, "123")

    // UI
    Image(
        painterResource(
            R.drawable.unknown
        ),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .requiredSize(100.dp),
        alignment = Alignment.Center
    )
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
            delay(15L)
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

@Composable
fun ShowText(text: String, fontSize: TextUnit) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = text,
        fontSize = fontSize
    )
}