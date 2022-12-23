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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.speakbyhand.app.R
import com.speakbyhand.app.core.DelimiterDetector
import com.speakbyhand.app.presentation.theme.SpeakByHandTheme
import java.util.*
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.Color

class SampleActivity : ComponentActivity(), DelimiterDetector.Listener {

    private var delimiterDetector: DelimiterDetector? = null
    private var sensorManager: SensorManager? = null
    private var vibrator: Vibrator? = null
    private var countDownTimer: CountDownTimer? = null
    private var performedGesture = false
    private var trigger = false
    private var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp { ShowText(stringResource(R.string.shake), fontSize = 30.sp) }
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        delimiterDetector = DelimiterDetector(this)
        delimiterDetector!!.start(sensorManager)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        textToSpeech = TextToSpeech(applicationContext) { status ->
            if (status != TextToSpeech.ERROR) textToSpeech!!.language = Locale.US
        }

    }

    override fun hearShake() {
        vibrator!!.vibrate(VibrationEffect.createOneShot(500, 10))
        delimiterDetector!!.stop()
        trigger = true
        countDownTimer = object : CountDownTimer(2000, 50) { //
            override fun onTick(millisUntilFinished: Long) {
                if (trigger) {
                    checkPerformedGesture()
                    setContent { WearApp { ShowTimer() } }
                }
                trigger = false
            }

            override fun onFinish() {
                finishedPerformedGesture()
            }
        }
        (countDownTimer as CountDownTimer).start()
    }

    private fun checkPerformedGesture() {
        performedGesture = true
        //performedGesture = false
    }

    private fun finishedPerformedGesture() {
        if (countDownTimer != null) countDownTimer!!.cancel()

        if (performedGesture) {
            textToSpeech!!.speak("SPEAK SPEAK SPEAK", TextToSpeech.QUEUE_FLUSH, null, null)
            setContent {
                WearApp { ShowImage() }
            }
        } else {
            setContent {
                WearApp {
                    ShowText(stringResource(R.string.unknown), fontSize = 25.sp)
                    ShowText(stringResource(R.string.question_mark), fontSize = 40.sp)
                }
            }
            val mVibratePattern = longArrayOf(0, 500, 500, 500)
            val effect = VibrationEffect.createWaveform(mVibratePattern, -1)
            vibrator!!.vibrate(effect)
        }

        countDownTimer = object : CountDownTimer(3000, 500) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                delimiterDetector!!.start(sensorManager)
                setContent {
                    WearApp { ShowText(stringResource(R.string.shake), fontSize = 30.sp) }
                }
            }
        }
        (countDownTimer as CountDownTimer).start()
    }
}

@Composable
fun ShowTimer() {
    Timer(
        totalTime = 2L * 1000L,
        inactiveBarColor = Color.DarkGray,
        activeBarColor = MaterialTheme.colors.primary,
        modifier = Modifier.size(200.dp)
    )
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

@Composable
fun ShowImage() {
    Image(
        painterResource(R.drawable.speaker),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .requiredSize(100.dp),
        alignment = Alignment.Center
    )
}

@Composable
fun WearApp(content: @Composable () -> Unit) {
    SpeakByHandTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp { ShowText(stringResource(R.string.app_name), fontSize = 30.sp) }
}
