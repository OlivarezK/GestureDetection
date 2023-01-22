package com.speakbyhand.app.core

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ShakeDetectorTest {
    @Test
    fun testNotMovingIsNotShaking() {
        val detector = ShakeDetector(5, 80f, 10)

        detector.update(0f,0f,0f) // 1
        detector.update(0f,0f,0f) // 2
        detector.update(0f,0f,0f) // 3
        detector.update(0f,0f,0f) // 4
        detector.update(0f,0f,0f) // 5
        detector.update(0f,0f,0f) // 6
        detector.update(0f,0f,0f) // 7
        detector.update(0f,0f,0f) // 8
        detector.update(0f,0f,0f) // 9
        detector.update(0f,0f,0f) // 10

        Assertions.assertEquals(false, detector.isShaking)
    }

    @Test
    fun testFastXMovementIsShaking() {
        val detector = ShakeDetector(5, 80f, 10)

        detector.update(100f,0f,0f) // 1
        detector.update(100f,0f,0f) // 2
        detector.update(100f,0f,0f) // 3
        detector.update(100f,0f,0f) // 4
        detector.update(100f,0f,0f) // 5
        detector.update(100f,0f,0f) // 6
        detector.update(100f,0f,0f) // 7
        detector.update(100f,0f,0f) // 8
        detector.update(100f,0f,0f) // 9
        detector.update(100f,0f,0f) // 10

        Assertions.assertEquals(true, detector.isShaking)
    }

    @Test
    fun testFastYMovementIsShaking() {
        val detector = ShakeDetector(5, 80f, 10)

        detector.update(0f, 100f, 0f) // 1
        detector.update(0f, 100f, 0f) // 2
        detector.update(0f, 100f, 0f) // 3
        detector.update(0f, 100f, 0f) // 4
        detector.update(0f, 100f, 0f) // 5
        detector.update(0f, 100f, 0f) // 6
        detector.update(0f, 100f, 0f) // 7
        detector.update(0f, 100f, 0f) // 8
        detector.update(0f, 100f, 0f) // 9
        detector.update(0f, 100f, 0f) // 10

        Assertions.assertEquals(true, detector.isShaking)
    }

    @Test
    fun testFastZMovementIsShaking() {
        val detector = ShakeDetector(5, 80f, 10)

        detector.update(0f, 0f, 100f) // 1
        detector.update(0f, 0f, 100f) // 2
        detector.update(0f, 0f, 100f) // 3
        detector.update(0f, 0f, 100f) // 4
        detector.update(0f, 0f, 100f) // 5
        detector.update(0f, 0f, 100f) // 6
        detector.update(0f, 0f, 100f) // 7
        detector.update(0f, 0f, 100f) // 8
        detector.update(0f, 0f, 100f) // 9
        detector.update(0f, 0f, 100f) // 10

        Assertions.assertEquals(true, detector.isShaking)
    }

    @Test
    fun testSlowXMovementIsNotShaking() {
        val detector = ShakeDetector(5, 80f, 10)

        detector.update(8f, 0f, 0f) // 1
        detector.update(8f, 0f, 0f) // 2
        detector.update(8f, 0f, 0f) // 3
        detector.update(8f, 0f, 0f) // 4
        detector.update(8f, 0f, 0f) // 5
        detector.update(8f, 0f, 0f) // 6
        detector.update(8f, 0f, 0f) // 7
        detector.update(8f, 0f, 0f) // 8
        detector.update(8f, 0f, 0f) // 9
        detector.update(8f, 0f, 0f) // 10

        Assertions.assertEquals(false, detector.isShaking)
    }

    @Test
    fun testSlowYMovementIsNotShaking() {
        val detector = ShakeDetector(5, 80f, 10)

        detector.update(0f, 8f, 0f) // 1
        detector.update(0f, 8f, 0f) // 2
        detector.update(0f, 8f, 0f) // 3
        detector.update(0f, 8f, 0f) // 4
        detector.update(0f, 8f, 0f) // 5
        detector.update(0f, 8f, 0f) // 6
        detector.update(0f, 8f, 0f) // 7
        detector.update(0f, 8f, 0f) // 8
        detector.update(0f, 8f, 0f) // 9
        detector.update(0f, 8f, 0f) // 10

        Assertions.assertEquals(false, detector.isShaking)
    }

    @Test
    fun testSlowZMovementIsNotShaking() {
        val detector = ShakeDetector(5, 80f, 10)

        detector.update(0f,0f, 8f) // 1
        detector.update(0f,0f, 8f) // 2
        detector.update(0f,0f, 8f) // 3
        detector.update(0f,0f, 8f) // 4
        detector.update(0f,0f, 8f) // 5
        detector.update(0f,0f, 8f) // 6
        detector.update(0f,0f, 8f) // 7
        detector.update(0f,0f, 8f) // 8
        detector.update(0f,0f, 8f) // 9
        detector.update(0f,0f, 8f) // 10

        Assertions.assertEquals(false, detector.isShaking)
    }

    @Test
    fun testAboveThresholdBoundaryIsShaking() {
        val detector = ShakeDetector(5, 80f, 10)

        detector.update(0f,0f, 10f) // 1
        detector.update(0f,0f, 10f) // 2
        detector.update(0f,0f, 10f) // 3
        detector.update(0f,0f, 10f) // 4
        detector.update(0f,0f, 10f) // 5
        detector.update(0f,0f, 10f) // 6
        detector.update(0f,0f, 0f) // 7
        detector.update(0f,0f, 0f) // 8
        detector.update(0f,0f, 0f) // 9
        detector.update(0f,0f, 0f) // 10

        Assertions.assertEquals(true, detector.isShaking)
    }

    @Test
    fun testOnThresholdBoundaryIsShaking() {
        val detector = ShakeDetector(5, 80f, 10)

        detector.update(0f,0f, 10f) // 1
        detector.update(0f,0f, 10f) // 2
        detector.update(0f,0f, 10f) // 3
        detector.update(0f,0f, 10f) // 4
        detector.update(0f,0f, 10f) // 5
        detector.update(0f,0f, 0f) // 6
        detector.update(0f,0f, 0f) // 7
        detector.update(0f,0f, 0f) // 8
        detector.update(0f,0f, 0f) // 9
        detector.update(0f,0f, 0f) // 10

        Assertions.assertEquals(true, detector.isShaking)
    }

    @Test
    fun testBelowThresholdBoundaryIsNotShaking() {
        val detector = ShakeDetector(5, 80f, 10)

        detector.update(0f,0f, 10f) // 1
        detector.update(0f,0f, 10f) // 2
        detector.update(0f,0f, 10f) // 3
        detector.update(0f,0f, 10f) // 4
        detector.update(0f,0f, 0f) // 5
        detector.update(0f,0f, 0f) // 6
        detector.update(0f,0f, 0f) // 7
        detector.update(0f,0f, 0f) // 8
        detector.update(0f,0f, 0f) // 9
        detector.update(0f,0f, 0f) // 10

        Assertions.assertEquals(false, detector.isShaking)
    }
}