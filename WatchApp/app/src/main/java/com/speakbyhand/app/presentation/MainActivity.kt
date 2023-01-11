/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.speakbyhand.app.presentation

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.speakbyhand.app.R
import com.speakbyhand.app.core.*
import com.speakbyhand.app.presentation.theme.SpeakByHandTheme
import kotlinx.coroutines.delay
import kotlin.concurrent.thread
import kotlin.math.abs
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

class MainActivity : ComponentActivity(), android.view.GestureDetector.OnGestureListener {

    // Declaring gesture detector, swipe threshold, and swipe velocity threshold
    private lateinit var swipeDetector: android.view.GestureDetector
    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100
    private var isSwiped = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // Logic
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val delimiterDetector = NewDelimiterDetector()
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        val textToSpeech = TextToSpeech(applicationContext) {}
        val gestureDataRecorder = GestureDataRecorder(this)
        val gestureDetector = GestureDetector(this)
        val gestureToPhrase = GestureCodeToPhraseConverter(textToSpeech)

        swipeDetector = android.view.GestureDetector(this)

        // UI
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
                                isSwiped
                            },
                            onModeChanged = {
                                isSwiped = false
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
                                isSwiped
                            },
                            onModeChanged = {
                                isSwiped = false
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
                            }
                        )
                        AppState.SpeakingPhrase -> SpeakingPhrase(
                            textToSpeech = textToSpeech,
                            phrase = gestureToPhrase.toMappedPhrase(detectedGestureCode),
                            gesture = detectedGestureCode,
                            onFinish = {
                                currentState = when(mode){
                                    AppMode.ShakeMode -> AppState.WaitingDelimiter
                                    AppMode.ButtonMode -> AppState.ButtonMode
                                }
                            }
                        )
                        AppState.UnknownGesture -> UnknownGesture(
                            vibrator = vibrator,
                            onFinish = {
                                currentState = when(mode){
                                    AppMode.ShakeMode -> AppState.WaitingDelimiter
                                    AppMode.ButtonMode -> AppState.ButtonMode
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Override this method to recognize touch event
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (swipeDetector.onTouchEvent(event)) {
            true
        }
        else {
            super.onTouchEvent(event)
        }
    }

    // All the below methods are GestureDetector.OnGestureListener members
    override fun onDown(e: MotionEvent?): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent?) {
        return
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
        return
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        try {
            val diffY = e2.y - e1.y
            val diffX = e2.x - e1.x
            if (abs(diffX) < abs(diffY)) {
                if (abs(diffY) > swipeThreshold && abs(velocityY) > swipeVelocityThreshold) {
                    if (diffX > 0) {
                        //Toast.makeText(applicationContext, "Switched mode", Toast.LENGTH_SHORT).show()
                        isSwiped = true
                    }
                }
            }
        }
        catch (exception: Exception) {
            exception.printStackTrace()
        }
        return true
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
        // Disabled for testing
        do {
            val isDelimiterDetected = detectDelimiter()
            val isSwitched = detectSwipe()
        } while (!isDelimiterDetected && !isSwitched)

//        detectDelimiter()
//        Thread.sleep(5000)

        if (detectSwipe()){
            onModeChanged()
        }else{
            onDelimiterDetected()

            //val mVibratePattern = longArrayOf(0, 500, 500, 500)
            val effect = VibrationEffect.createOneShot(500, -1)
            vibrator.vibrate(effect)
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
    onFinish: () -> Unit
) {
    // Logic
    val startTime = System.currentTimeMillis();
    do {
        val currentTime = System.currentTimeMillis()
    } while (currentTime - startTime < 1200)

    onStart()

    object : CountDownTimer(3000, 1000) {
        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {
            val detection = detectGestureCode()
            val hasDetectedGesture = detection.first
            val detectedGestureCode = detection.second
            if (hasDetectedGesture) {
                onGestureDetected(detectedGestureCode)
            } else {
                onGestureNotDetected()
            }
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
    textToSpeech.speak(phrase, TextToSpeech.QUEUE_FLUSH, null, null)
    thread {
        do {
            val speakingEnd = textToSpeech.isSpeaking
        } while (speakingEnd)
        onFinish()
    }

    // UI
    gestureDisplay(gesture)
}

@Composable
fun gestureDisplay(gesture: GestureCode){
    Image(
        painterResource(
            when(gesture){
                GestureCode.Yes -> R.drawable.yes
                GestureCode.Help -> R.drawable.help
                GestureCode.DrinkWater -> R.drawable.drink
                GestureCode.EatFood -> R.drawable.eat
                GestureCode.No -> R.drawable.no
                else -> {R.drawable.yes}
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