@file:OptIn(ExperimentalWearMaterialApi::class)

package com.speakbyhand.app.core

import android.hardware.SensorManager
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.rememberSwipeableState
import com.speakbyhand.app.presentation.*
import org.junit.Rule
import org.junit.Test
import java.util.*
import com.speakbyhand.app.R

class SwatchAppUiIntegrationTest {
    @get:Rule
    val rule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext;
    private val sensorManager = context.getSystemService(ComponentActivity.SENSOR_SERVICE) as SensorManager
    private val shakeDetector = ShakeDetector(2, 10f, 5)
    private val delimiterDetector = DelimiterDetector(shakeDetector, sensorManager)
    private val vibrator = context.getSystemService(ComponentActivity.VIBRATOR_SERVICE) as Vibrator
    private val textToSpeech = TextToSpeech(context) {}
    private val gestureDataRecorder = GestureDataRecorder(context)
    private val gestureDetector = GestureDetector(context, "gesture_conv_model_nv2.tflite")

    // Waiting Delimiter State Test
    @Test
    fun testUiChangesToPerformingGestureWhenShakeIsDetected() {
        rule.setContent {
            var currentState by rememberSaveable { mutableStateOf(AppState.WaitingDelimiter) }

            when (currentState) {
                AppState.WaitingDelimiter -> WaitingTrigger(
                    delimiterDetector = delimiterDetector,
                    vibrator = vibrator,
                    sensorManager = sensorManager,
                    onTrigger = {
                        currentState = AppState.PerformingGesture
                    },
                )
                AppState.PerformingGesture -> PerformingGesture(
                    onGestureDetected = {},
                    onGestureNotDetected = {},
                    vibrator = vibrator,
                    gestureDataRecorder = gestureDataRecorder,
                    gestureDetector = gestureDetector
                )
                else -> {}
            }
        }

        shakeDetector.update(100f, 0f, 0f)
        shakeDetector.update(100f, 0f, 0f)
        shakeDetector.update(100f, 0f, 0f)
        shakeDetector.update(100f, 0f, 0f)

        rule.waitUntil(2000) {
            rule.onAllNodesWithText("PERFORMING GESTURE", substring = true, ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        rule.onNodeWithText("PERFORMING GESTURE").assertExists()
    }

    @Test
    fun testUiChangesToButtonModeWhenSwipeIsDetected() {
        rule.setContent {
            var currentState by rememberSaveable { mutableStateOf(AppState.WaitingDelimiter) }

            when (currentState) {
                AppState.WaitingDelimiter -> WaitingTrigger(
                    delimiterDetector = delimiterDetector,
                    vibrator = vibrator,
                    sensorManager = sensorManager,
                    onTrigger = {
                        currentState = AppState.PerformingGesture
                    },
                )
                else -> {}
            }
        }

        rule.onRoot().performTouchInput { swipeUp() }

        rule.waitUntil {
            rule.onAllNodesWithText("START", substring = true, ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        rule.onNodeWithText("START").assertExists()
    }


    // Button Mode State Test
    @Test
    fun testUiChangesToPerformingGestureWhenButtonIsPressed() {
        rule.setContent {
            var currentState by rememberSaveable { mutableStateOf(AppState.WaitingDelimiter) }

            when (currentState) {
                AppState.WaitingDelimiter -> WaitingTrigger(
                    delimiterDetector = delimiterDetector,
                    vibrator = vibrator,
                    sensorManager = sensorManager,
                    onTrigger = {
                        currentState = AppState.PerformingGesture
                    },
                )
                AppState.PerformingGesture -> PerformingGesture(
                    onGestureDetected = {},
                    onGestureNotDetected = {},
                    vibrator = vibrator,
                    gestureDataRecorder = gestureDataRecorder,
                    gestureDetector = gestureDetector
                )
                else -> {}
            }
        }

        rule.onRoot().performTouchInput { swipeUp() }
        rule.onRoot().performClick()

        rule.waitUntil {
            rule.onAllNodesWithText("PERFORMING GESTURE", substring = true, ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        rule.onNodeWithText("PERFORMING GESTURE").assertExists()
    }

    @Test
    fun testUiChangesToWaitingDelimiterWhenSwipeIsDetected() {
        rule.setContent {
            var currentState by rememberSaveable { mutableStateOf(AppState.WaitingDelimiter) }

            when (currentState) {
                AppState.WaitingDelimiter -> WaitingTrigger(
                    delimiterDetector = delimiterDetector,
                    vibrator = vibrator,
                    sensorManager = sensorManager,
                    onTrigger = {
                        currentState = AppState.PerformingGesture
                    },
                )
                AppState.PerformingGesture -> PerformingGesture(
                    onGestureDetected = {},
                    onGestureNotDetected = {},
                    vibrator = vibrator,
                    gestureDataRecorder = gestureDataRecorder,
                    gestureDetector = gestureDetector
                )
                else -> {}
            }
        }

        rule.onRoot().performTouchInput { swipeUp() }
        rule.onRoot().performTouchInput { swipeDown() }

        rule.waitUntil {
            rule.onAllNodesWithText("SHAKE TO START", substring = true, ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        rule.onNodeWithText("SHAKE TO START").assertExists()
    }

    // Performing Gesture State Test
    @Test
    fun testUiChangesToSpeakingPhraseWhenGestureIsDetected() {
        rule.setContent {
            var currentState by rememberSaveable { mutableStateOf(AppState.PerformingGesture) }
            var detectedGestureCode by rememberSaveable { mutableStateOf(GestureCode.Unknown) }

            when (currentState) {
                AppState.PerformingGesture -> PerformingGesture(
                    vibrator = vibrator,
                    gestureDataRecorder = gestureDataRecorder,
                    gestureDetector = gestureDetector,
                    onGestureDetected = {
                        detectedGestureCode = it
                        currentState = AppState.SpeakingPhrase
                    },
                    onGestureNotDetected = {
                        currentState = AppState.UnknownGesture
                    }
                )
                AppState.SpeakingPhrase -> SpeakingPhrase(
                    textToSpeech = textToSpeech,
                    phrase = MappedPhrases.fromGestureCode(detectedGestureCode),
                    gesture = detectedGestureCode,
                    onFinish = {}
                )
                else -> {}
            }

            gestureDataRecorder.stop()
            gestureDataRecorder.reset()
            gestureDataRecorder.gestureData = GestureData(loadResourceAsString("/Drink_17_n_n"))
        }


        rule.waitUntil(5000) {
            rule.onAllNodesWithTag(R.drawable.drink.toString()).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithTag(R.drawable.drink.toString()).assertIsDisplayed()
    }

    @Test
    fun testUiChangesToUnknownGestureWhenNoGestureIsDetected() {
        rule.setContent {
            var currentState by rememberSaveable { mutableStateOf(AppState.PerformingGesture) }
            var detectedGestureCode by rememberSaveable { mutableStateOf(GestureCode.Unknown) }

            when (currentState) {
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
                AppState.UnknownGesture -> UnknownGesture(
                    onFinish = {}, textToSpeech = textToSpeech
                )
                else -> {}
            }
        }

        rule.waitUntil(5000) {
            rule.onAllNodesWithTag(R.drawable.unknown.toString()).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithTag(R.drawable.unknown.toString()).assertIsDisplayed()
    }

    @Test
    fun testUiChangesToUnknownGestureWhenNoMovementIsDetected() {
        gestureDataRecorder.reset()

        rule.setContent {
            var currentState by rememberSaveable { mutableStateOf(AppState.PerformingGesture) }
            var detectedGestureCode by rememberSaveable { mutableStateOf(GestureCode.Unknown) }
            when (currentState) {
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
                AppState.UnknownGesture -> UnknownGesture(
                    onFinish = {}, textToSpeech = textToSpeech
                )
                else -> {}
            }
        }

        rule.waitUntil(5000) {
            rule.onAllNodesWithTag(R.drawable.unknown.toString()).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithTag(R.drawable.unknown.toString()).assertIsDisplayed()
    }

    // Speaking Phrase State Test

    @Test
    fun testToiletIsOutputWhenToiletGestureIsPerformed() {
        val fileName = "/Toilet_178_n"
        val expectedImage = R.drawable.toilet
        rule.setContent {
            var currentState by rememberSaveable { mutableStateOf(AppState.PerformingGesture) }
            var detectedGestureCode by rememberSaveable { mutableStateOf(GestureCode.Unknown) }

            when (currentState) {
                AppState.PerformingGesture -> PerformingGesture(
                    vibrator = vibrator,
                    gestureDataRecorder = gestureDataRecorder,
                    gestureDetector = gestureDetector,
                    onGestureDetected = {
                        detectedGestureCode = it
                        currentState = AppState.SpeakingPhrase
                    },
                    onGestureNotDetected = {
                        currentState = AppState.UnknownGesture
                    }
                )
                AppState.SpeakingPhrase -> SpeakingPhrase(
                    textToSpeech = textToSpeech,
                    phrase = MappedPhrases.fromGestureCode(detectedGestureCode),
                    gesture = detectedGestureCode,
                    onFinish = {}
                )
                else -> {}
            }

            gestureDataRecorder.stop()
            gestureDataRecorder.reset()
            gestureDataRecorder.gestureData = GestureData(loadResourceAsString(fileName))
        }


        rule.waitUntil(5000) {
            rule.onAllNodesWithTag(expectedImage.toString()).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithTag(expectedImage.toString()).assertIsDisplayed()
    }

    @Test
    fun testFoodIsOutputWhenFoodGestureIsPerformed() {
        val fileName = "/Eat_7_n"
        val expectedImage = R.drawable.eat
        rule.setContent {
            var currentState by rememberSaveable { mutableStateOf(AppState.PerformingGesture) }
            var detectedGestureCode by rememberSaveable { mutableStateOf(GestureCode.Unknown) }

            when (currentState) {
                AppState.PerformingGesture -> PerformingGesture(
                    vibrator = vibrator,
                    gestureDataRecorder = gestureDataRecorder,
                    gestureDetector = gestureDetector,
                    onGestureDetected = {
                        detectedGestureCode = it
                        currentState = AppState.SpeakingPhrase
                    },
                    onGestureNotDetected = {
                        currentState = AppState.UnknownGesture
                    }
                )
                AppState.SpeakingPhrase -> SpeakingPhrase(
                    textToSpeech = textToSpeech,
                    phrase = MappedPhrases.fromGestureCode(detectedGestureCode),
                    gesture = detectedGestureCode,
                    onFinish = {}
                )
                else -> {}
            }

            gestureDataRecorder.stop()
            gestureDataRecorder.reset()
            gestureDataRecorder.gestureData = GestureData(loadResourceAsString(fileName))
        }


        rule.waitUntil(5000) {
            rule.onAllNodesWithTag(expectedImage.toString()).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithTag(expectedImage.toString()).assertIsDisplayed()
    }

    @Test
    fun testDrinkIsOutputWhenDrinkGestureIsPerformed() {
        val fileName = "/Drink_17_n_n"
        val expectedImage = R.drawable.drink

        rule.setContent {
            var currentState by rememberSaveable { mutableStateOf(AppState.PerformingGesture) }
            var detectedGestureCode by rememberSaveable { mutableStateOf(GestureCode.Unknown) }

            when (currentState) {
                AppState.PerformingGesture -> PerformingGesture(
                    vibrator = vibrator,
                    gestureDataRecorder = gestureDataRecorder,
                    gestureDetector = gestureDetector,
                    onGestureDetected = {
                        detectedGestureCode = it
                        currentState = AppState.SpeakingPhrase
                    },
                    onGestureNotDetected = {
                        currentState = AppState.UnknownGesture
                    }
                )
                AppState.SpeakingPhrase -> SpeakingPhrase(
                    textToSpeech = textToSpeech,
                    phrase = MappedPhrases.fromGestureCode(detectedGestureCode),
                    gesture = detectedGestureCode,
                    onFinish = {}
                )
                else -> {}
            }

            gestureDataRecorder.stop()
            gestureDataRecorder.reset()
            gestureDataRecorder.gestureData = GestureData(loadResourceAsString(fileName))
        }


        rule.waitUntil(5000) {
            rule.onAllNodesWithTag(expectedImage.toString()).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithTag(expectedImage.toString()).assertIsDisplayed()
    }

    @Test
    fun testHelpIsOutputWhenHelpGestureIsPerformed() {
        val fileName = "/Help_25_n"
        val expectedImage = R.drawable.help

        rule.setContent {
            var currentState by rememberSaveable { mutableStateOf(AppState.PerformingGesture) }
            var detectedGestureCode by rememberSaveable { mutableStateOf(GestureCode.Unknown) }

            when (currentState) {
                AppState.PerformingGesture -> PerformingGesture(
                    vibrator = vibrator,
                    gestureDataRecorder = gestureDataRecorder,
                    gestureDetector = gestureDetector,
                    onGestureDetected = {
                        detectedGestureCode = it
                        currentState = AppState.SpeakingPhrase
                    },
                    onGestureNotDetected = {
                        currentState = AppState.UnknownGesture
                    }
                )
                AppState.SpeakingPhrase -> SpeakingPhrase(
                    textToSpeech = textToSpeech,
                    phrase = MappedPhrases.fromGestureCode(detectedGestureCode),
                    gesture = detectedGestureCode,
                    onFinish = {}
                )
                else -> {}
            }

            gestureDataRecorder.stop()
            gestureDataRecorder.reset()
            gestureDataRecorder.gestureData = GestureData(loadResourceAsString(fileName))
        }


        rule.waitUntil(5000) {
            rule.onAllNodesWithTag(expectedImage.toString()).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithTag(expectedImage.toString()).assertIsDisplayed()
    }

    @Test
    fun testYesIsOutputWhenYesGestureIsPerformed() {
        val fileName = "/Yes_35_n"
        val expectedImage = R.drawable.yes

        rule.setContent {
            var currentState by rememberSaveable { mutableStateOf(AppState.PerformingGesture) }
            var detectedGestureCode by rememberSaveable { mutableStateOf(GestureCode.Unknown) }

            when (currentState) {
                AppState.PerformingGesture -> PerformingGesture(
                    vibrator = vibrator,
                    gestureDataRecorder = gestureDataRecorder,
                    gestureDetector = gestureDetector,
                    onGestureDetected = {
                        detectedGestureCode = it
                        currentState = AppState.SpeakingPhrase
                    },
                    onGestureNotDetected = {
                        currentState = AppState.UnknownGesture
                    }
                )
                AppState.SpeakingPhrase -> SpeakingPhrase(
                    textToSpeech = textToSpeech,
                    phrase = MappedPhrases.fromGestureCode(detectedGestureCode),
                    gesture = detectedGestureCode,
                    onFinish = {}
                )
                else -> {}
            }

            gestureDataRecorder.stop()
            gestureDataRecorder.reset()
            gestureDataRecorder.gestureData = GestureData(loadResourceAsString(fileName))
        }


        rule.waitUntil(5000) {
            rule.onAllNodesWithTag(expectedImage.toString()).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithTag(expectedImage.toString()).assertIsDisplayed()
    }

    @Test
    fun testNoIsOutputWhenNoGestureIsPerformed() {
        val fileName = "/No_113_n"
        val expectedImage = R.drawable.no

        rule.setContent {
            var currentState by rememberSaveable { mutableStateOf(AppState.PerformingGesture) }
            var detectedGestureCode by rememberSaveable { mutableStateOf(GestureCode.Unknown) }

            when (currentState) {
                AppState.PerformingGesture -> PerformingGesture(
                    vibrator = vibrator,
                    gestureDataRecorder = gestureDataRecorder,
                    gestureDetector = gestureDetector,
                    onGestureDetected = {
                        detectedGestureCode = it
                        currentState = AppState.SpeakingPhrase
                    },
                    onGestureNotDetected = {
                        currentState = AppState.UnknownGesture
                    }
                )
                AppState.SpeakingPhrase -> SpeakingPhrase(
                    textToSpeech = textToSpeech,
                    phrase = MappedPhrases.fromGestureCode(detectedGestureCode),
                    gesture = detectedGestureCode,
                    onFinish = {}
                )
                else -> {}
            }

            gestureDataRecorder.stop()
            gestureDataRecorder.reset()
            gestureDataRecorder.gestureData = GestureData(loadResourceAsString(fileName))
        }

        rule.waitUntil(5000) {
            rule.onAllNodesWithTag(expectedImage.toString()).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithTag(expectedImage.toString()).assertIsDisplayed()
    }

    @Test
    fun testNotRecognizedIsOutputWhenUnknownGestureIsPerformed() {
        val fileName = "/Unknown_1"
        val expectedImage = R.drawable.unknown

        rule.setContent {
            var currentState by rememberSaveable { mutableStateOf(AppState.PerformingGesture) }
            var detectedGestureCode by rememberSaveable { mutableStateOf(GestureCode.Unknown) }

            when (currentState) {
                AppState.PerformingGesture -> PerformingGesture(
                    vibrator = vibrator,
                    gestureDataRecorder = gestureDataRecorder,
                    gestureDetector = gestureDetector,
                    onGestureDetected = {
                        detectedGestureCode = it
                        currentState = AppState.SpeakingPhrase
                    },
                    onGestureNotDetected = {
                        currentState = AppState.UnknownGesture
                    }
                )
                AppState.SpeakingPhrase -> SpeakingPhrase(
                    textToSpeech = textToSpeech,
                    phrase = MappedPhrases.fromGestureCode(detectedGestureCode),
                    gesture = detectedGestureCode,
                    onFinish = {}
                )
                else -> {}
            }

            gestureDataRecorder.stop()
            gestureDataRecorder.reset()
            gestureDataRecorder.gestureData = GestureData(loadResourceAsString(fileName))
        }

        rule.waitUntil(5000) {
            rule.onAllNodesWithTag(expectedImage.toString()).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithTag(expectedImage.toString()).assertIsDisplayed()
    }

    @Test
    fun testSpeakingPhraseUiChangesToWaitingDelimiterWhenAppIsInShakeMode() {
        val initialTriggerMode = TriggerMode.ShakeMode
        rule.setContent {
            var currentState by rememberSaveable { mutableStateOf(AppState.SpeakingPhrase) }
            val swipeState = rememberSwipeableState(initialTriggerMode)

            when (currentState) {
                AppState.WaitingDelimiter -> WaitingTrigger(
                    swipeState = swipeState,
                    delimiterDetector = delimiterDetector,
                    vibrator = vibrator,
                    sensorManager = sensorManager,
                    onTrigger = {
                        currentState = AppState.PerformingGesture
                    },
                )
                AppState.SpeakingPhrase -> SpeakingPhrase(
                    textToSpeech = textToSpeech,
                    phrase = MappedPhrases.fromGestureCode(GestureCode.DrinkWater),
                    gesture = GestureCode.DrinkWater,
                    onFinish = {
                        currentState = AppState.WaitingDelimiter
                    }
                )
                else -> {}
            }
        }

        rule.waitUntil(5000) {
            rule.onAllNodesWithText("SHAKE TO START", substring = true, ignoreCase = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        rule.onNodeWithText("SHAKE TO START").assertExists()
    }

    @Test
    fun testSpeakingPhraseUiChangesToPressButtonWhenAppIsInButtonMode() {
        val initialTriggerMode = TriggerMode.ButtonMode
        rule.setContent {
            var currentState by rememberSaveable { mutableStateOf(AppState.SpeakingPhrase) }
            val swipeState = rememberSwipeableState(initialTriggerMode)

            when (currentState) {
                AppState.WaitingDelimiter -> WaitingTrigger(
                    swipeState = swipeState,
                    delimiterDetector = delimiterDetector,
                    vibrator = vibrator,
                    sensorManager = sensorManager,
                    onTrigger = {
                        currentState = AppState.PerformingGesture
                    },
                )
                AppState.SpeakingPhrase -> SpeakingPhrase(
                    textToSpeech = textToSpeech,
                    phrase = MappedPhrases.fromGestureCode(GestureCode.DrinkWater),
                    gesture = GestureCode.DrinkWater,
                    onFinish = {
                        currentState = AppState.WaitingDelimiter
                    }
                )
                else -> {}
            }
        }

        rule.waitUntil(5000) {
            rule.onAllNodesWithText("START", substring = true, ignoreCase = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        rule.onNodeWithText("START").assertExists()
    }


    // Unknown Gesture State Test

    @Test
    fun testGestureNotRecognizedIsOutput() {
        rule.setContent {
            var currentState by rememberSaveable { mutableStateOf(AppState.UnknownGesture) }
            when (currentState) {
                AppState.UnknownGesture -> UnknownGesture(
                    onFinish = {
                    },
                    textToSpeech = textToSpeech
                )
                else -> {}
            }
        }

        val expectedImage = R.drawable.unknown
        rule.waitUntil(5000) {
            rule.onAllNodesWithTag(expectedImage.toString()).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithTag(expectedImage.toString()).assertIsDisplayed()
    }

    @Test
    fun testUnknownGestureUiChangesToWaitingDelimiterWhenAppIsInShakeMode() {
        val initialTriggerMode = TriggerMode.ShakeMode
        rule.setContent {
            var currentState by rememberSaveable { mutableStateOf(AppState.UnknownGesture) }
            val swipeState = rememberSwipeableState(initialTriggerMode)

            when (currentState) {
                AppState.WaitingDelimiter -> WaitingTrigger(
                    swipeState = swipeState,
                    delimiterDetector = delimiterDetector,
                    vibrator = vibrator,
                    sensorManager = sensorManager,
                    onTrigger = {
                        currentState = AppState.PerformingGesture
                    },
                )
                AppState.UnknownGesture -> UnknownGesture(
                    onFinish = {
                        currentState = AppState.WaitingDelimiter
                    },
                    textToSpeech = textToSpeech
                )
                else -> {}
            }
        }

        rule.waitUntil(5000) {
            rule.onAllNodesWithText("SHAKE TO START", substring = true, ignoreCase = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        rule.onNodeWithText("SHAKE TO START").assertExists()
    }

    @Test
    fun testUnknownGestureUiChangesToPressButtonWhenAppIsInButtonMode() {
        val initialTriggerMode = TriggerMode.ButtonMode
        rule.setContent {
            var currentState by rememberSaveable { mutableStateOf(AppState.UnknownGesture) }
            val swipeState = rememberSwipeableState(initialTriggerMode)

            when (currentState) {
                AppState.WaitingDelimiter -> WaitingTrigger(
                    swipeState = swipeState,
                    delimiterDetector = delimiterDetector,
                    vibrator = vibrator,
                    sensorManager = sensorManager,
                    onTrigger = {
                        currentState = AppState.PerformingGesture
                    },
                )
                AppState.UnknownGesture -> UnknownGesture(
                    onFinish = {
                        currentState = AppState.WaitingDelimiter
                    },
                    textToSpeech = textToSpeech
                )
                else -> {}
            }
        }

        rule.waitUntil(5000) {
            rule.onAllNodesWithText("START", substring = true, ignoreCase = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        rule.onNodeWithText("START").assertExists()
    }

    fun loadResourceAsString(fileName: String): String {
        val scanner =
            Scanner(SwatchAppUiIntegrationTest::class.java.getResourceAsStream(fileName));
        val contents = scanner.useDelimiter("\\A").next();
        scanner.close();
        return contents;
    }
}