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
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.TabRow
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.wear.compose.material.*
import androidx.wear.compose.material.SwipeableDefaults.resistanceConfig
import com.google.accompanist.pager.*
import com.speakbyhand.app.R
import com.speakbyhand.app.core.*
import com.speakbyhand.app.presentation.theme.SpeakByHandTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt


enum class AppState {
    WaitingDelimiter,
    PerformingGesture,
    SpeakingPhrase,
    UnknownGesture,
    Splash
}

enum class TriggerMode {
    ShakeMode,
    ButtonMode
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val sensorManager = getSystemService(ComponentActivity.SENSOR_SERVICE) as SensorManager
        val shakeDetector = ShakeDetector(15, 80f, 50)
        val delimiterDetector = DelimiterDetector(shakeDetector, sensorManager)
        val vibrator = getSystemService(ComponentActivity.VIBRATOR_SERVICE) as Vibrator
        val textToSpeech = TextToSpeech(applicationContext) {}
        val gestureDataRecorder = GestureDataRecorder(this)
        val gestureDetector = GestureDetector(this, "gesture_conv_model_n_n.tflite")


        setContent {
            SpeakByHandTheme {
                WatchApp(
                    delimiterDetector,
                    vibrator,
                    textToSpeech,
                    gestureDataRecorder,
                    gestureDetector,
                    sensorManager,
                )
            }
        }
    }


}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun WatchApp(
    delimiterDetector: DelimiterDetector,
    vibrator: Vibrator,
    textToSpeech: TextToSpeech,
    gestureDataRecorder: GestureDataRecorder,
    gestureDetector: GestureDetector,
    sensorManager: SensorManager,
    initialState: AppState = AppState.Splash
) {
    var currentState by rememberSaveable { mutableStateOf(initialState) }
    var detectedGestureCode by rememberSaveable { mutableStateOf(GestureCode.Sample) }
    val swipeState = rememberSwipeableState(TriggerMode.ShakeMode)

    when (currentState) {
        AppState.Splash -> SplashScreen(
            onFinish = {
                currentState = AppState.WaitingDelimiter
            }
        )
        AppState.WaitingDelimiter -> WaitingTrigger(
            delimiterDetector = delimiterDetector,
            vibrator = vibrator,
            sensorManager = sensorManager,
            onTrigger = {
                currentState = AppState.PerformingGesture
            },
            swipeState = swipeState
        )
        AppState.PerformingGesture -> PerformingGesture(
            onGestureDetected = {
                detectedGestureCode = it
                currentState = AppState.SpeakingPhrase
            },
            onGestureNotDetected = {
                currentState = AppState.UnknownGesture
            },
            vibrator = vibrator,
            gestureDataRecorder = gestureDataRecorder,
            gestureDetector = gestureDetector
        )
        AppState.SpeakingPhrase -> SpeakingPhrase(
            textToSpeech = textToSpeech,
            phrase = MappedPhrases.fromGestureCode(detectedGestureCode),
            gesture = detectedGestureCode,
            onFinish = {
                currentState = AppState.WaitingDelimiter
            }
        )
        AppState.UnknownGesture -> UnknownGesture(
            onFinish = {
                currentState = AppState.WaitingDelimiter
            },
            textToSpeech = textToSpeech
        )
        else -> {
            Text("This is not supposed to show :/")
        }
    }

}

@Composable
fun SplashScreen(
    onFinish: () -> Unit
){
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if(startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000
        )
    )

    LaunchedEffect(key1 = true){
        startAnimation = true
        delay(2000)
        onFinish()
    }

    // UI
    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
            .alpha(alphaAnim.value),
        contentAlignment = Alignment.Center
    ){
        Image(
            modifier = Modifier.size(170.dp),
            painter = painterResource(id = R.drawable.splash),
            contentDescription = "Logo Icon"
        )
    }
}

@OptIn(ExperimentalWearMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun WaitingTrigger(
    delimiterDetector: DelimiterDetector,
    vibrator: Vibrator,
    sensorManager: SensorManager,
    onTrigger: () -> Unit,
    swipeState: SwipeableState<TriggerMode> = rememberSwipeableState(TriggerMode.ShakeMode)
) {
    val verticalAnchors = mapOf(0f to TriggerMode.ButtonMode, 1f to TriggerMode.ShakeMode)
    val pagerSelect = rememberPagerState(pageCount = 2)
    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            .padding(end = 5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.End
    ) {
        VerticalPagerIndicator(
            pagerState = pagerSelect,
            activeColor = Color.hsv(261f, 0.43f, 0.80f),
            inactiveColor = Color.LightGray
        )
    }

    when(swipeState.currentValue){
        TriggerMode.ShakeMode -> LaunchedEffect(key1 = true){scope.launch { pagerSelect.animateScrollToPage(0) }}
        TriggerMode.ButtonMode -> LaunchedEffect(key1 = true){scope.launch { pagerSelect.animateScrollToPage(1) }}
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .swipeable(
                state = swipeState,
                anchors = verticalAnchors,
                orientation = Orientation.Vertical,
                thresholds = { _, _ -> FractionalThreshold(0.2f) },
                resistance = resistanceConfig(verticalAnchors.keys),
                velocityThreshold = Dp(200F)
            ),
        contentAlignment = Alignment.CenterStart,
        ) {
        when (swipeState.currentValue) {
            TriggerMode.ShakeMode -> ShakeMode(
                onStart = {
                    delimiterDetector.start(sensorManager)
                },
                detectDelimiter = {
                    delimiterDetector.isDelimiterDetected
                },
                onDelimiterDetected = {
                    onTrigger()
                },
                onFinish = {
                    delimiterDetector.stop()
                },
                vibrator = vibrator
            )
            TriggerMode.ButtonMode -> ButtonMode(
                onClick = {
                    onTrigger()
                },
                delimiterOnFinish = {
                    delimiterDetector.stop()
                },
                vibrator = vibrator
            )
            else -> Text("This is not supposed to show (2) :/")
        }

    }

}

@Composable
fun ShakeMode(
    onStart: () -> Unit,
    detectDelimiter: () -> Boolean,
    onDelimiterDetected: () -> Unit,
    onFinish: () -> Unit,
    vibrator: Vibrator
) {
    // Logic
    onStart()

    thread {
        // wait for delimiter
        do {
            val isDelimiterDetected = detectDelimiter()
        } while (!isDelimiterDetected)

        // play vibration
        val effect = VibrationEffect.createOneShot(500, -1)
        vibrator.vibrate(effect)

        // wait for vibration to finish
        val startTime = System.currentTimeMillis()
        do {
            val currentTime = System.currentTimeMillis()
        } while (currentTime - startTime < 500)

        onDelimiterDetected()

        onFinish()
    }

    // UI
    ShowText(stringResource(R.string.shake), fontSize = 30.sp)

}

@Composable
fun ButtonMode(
    onClick: () -> Unit,
    delimiterOnFinish: () -> Unit,
    vibrator: Vibrator
) {
    // Logic
    delimiterOnFinish() // unregister waiting delimiters

    //UI
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                onClick()

                val effect = VibrationEffect.createOneShot(500, -1)
                vibrator.vibrate(effect)
            },
            modifier = Modifier.size(120.dp)
        ) {
            Text(text = "START", fontSize = 20.sp)
        }
    }
}

@Composable
fun PerformingGesture(
    onGestureDetected: (GestureCode) -> Unit,
    onGestureNotDetected: () -> Unit,
    vibrator: Vibrator,
    gestureDataRecorder: GestureDataRecorder,
    gestureDetector: GestureDetector
) {
//    var moved = false
    gestureDataRecorder.start()

    object : CountDownTimer(3000, 10) {
        override fun onTick(millisUntilFinished: Long) {
//            val notMoving = gestureDataRecorder.isPaused
//            Log.i("moved", moved.toString())
//
//            if (!notMoving) {
//                moved = true
//            }
        }

        override fun onFinish() {
//            if (moved) {
//                val detection = gestureDetector.detect(gestureDataRecorder.data)
//                onGestureDetected(detection)
//            } else {
//                onGestureNotDetected()
//            }

            val detection = gestureDetector.detect(gestureDataRecorder.data)
            onGestureDetected(detection)

            val effect = VibrationEffect.createOneShot(500, -1)
            vibrator.vibrate(effect)

            gestureDataRecorder.reset()
            gestureDataRecorder.stop()
        }
    }.start()

    Timer(
        totalTime = 3L * 1000L,
        inactiveBarColor = Color.DarkGray,
        activeBarColor = MaterialTheme.colors.primary,
        modifier = Modifier.size(200.dp)
    )
}

@Composable
fun SpeakingPhrase(
    textToSpeech: TextToSpeech,
    phrase: String,
    onFinish: () -> Unit,
    gesture: GestureCode
) {
    // Logic
    textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
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
fun GestureDisplay(gesture: GestureCode) {
    val resId = when (gesture) {
        GestureCode.Yes -> R.drawable.yes
        GestureCode.Help -> R.drawable.help
        GestureCode.DrinkWater -> R.drawable.drink
        GestureCode.EatFood -> R.drawable.eat
        GestureCode.No -> R.drawable.no
        GestureCode.Toilet -> R.drawable.toilet
        else -> {
            R.drawable.unknown
        }
    }
    Image(
        painter = painterResource(id = resId),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .requiredSize(100.dp)
            .testTag(resId.toString()),
        alignment = Alignment.Center
    )
}

@Composable
fun UnknownGesture(onFinish: () -> Unit, textToSpeech: TextToSpeech) {
    // Logic
    textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
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
    val resId = R.drawable.unknown
    Image(
        painterResource(resId),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .requiredSize(100.dp)
            .testTag(resId.toString()),
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
            .fillMaxSize()
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