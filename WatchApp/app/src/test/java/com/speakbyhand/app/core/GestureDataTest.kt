package com.speakbyhand.app.core

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class GestureDataTest {
    @Test
    fun testGestureDataFromCsvIsCreatedProperly() {
        val csv = """
            1.1,1.2,1.3,1.4,1.5,1.6,1.7
            2.1,2.2,2.3,2.4,2.5,2.6,2.7
            3.1,3.2,3.3,3.4,3.5,3.6,3.7
        """.trimIndent()
        val dataFromCsv = GestureData(csv)
        val dataFromManual = GestureData()

        dataFromManual.addTime(1.1f)
        dataFromManual.addAccelerometerData(1.2f, 1.3f, 1.4f)
        dataFromManual.addGyroscopeData(1.5f, 1.6f, 1.7f)

        dataFromManual.addTime(2.1f)
        dataFromManual.addAccelerometerData(2.2f, 2.3f, 2.4f)
        dataFromManual.addGyroscopeData(2.5f, 2.6f, 2.7f)

        dataFromManual.addTime(3.1f)
        dataFromManual.addAccelerometerData(3.2f, 3.3f, 3.4f)
        dataFromManual.addGyroscopeData(3.5f, 3.6f, 3.7f)

        Assertions.assertEquals(dataFromManual, dataFromCsv)
    }

    @Test
    fun testGestureDataToFloatArrayIsFormattedProperly() {
        val dataFromManual = GestureData()

        for (i in 1..GestureData.dataPointCount) {
            dataFromManual.addTime(i + .1f)
            dataFromManual.addAccelerometerData(i + .2f, i + .3f, i + .4f)
            dataFromManual.addGyroscopeData(i + .5f, i + .6f, i + .7f)
        }

        val array = mutableListOf<Float>()
        for (i in 1..GestureData.dataPointCount) {
            array.addAll(arrayOf(i + .2f, i + .3f, i + .4f, i + .5f, i + .6f, i + .7f))
        }

        Assertions.assertEquals(array, dataFromManual.toArray().toMutableList())

    }

    @Test
    fun testGestureDataToArrayTruncatesExcessData() {
        val dataFromManual = GestureData()

        for (i in 1..GestureData.dataPointCount + 100) {
            dataFromManual.addTime(i + .1f)
            dataFromManual.addAccelerometerData(i + .2f, i + .3f, i + .4f)
            dataFromManual.addGyroscopeData(i + .5f, i + .6f, i + .7f)
        }

        val array = mutableListOf<Float>()
        for (i in 1..GestureData.dataPointCount) {
            array.addAll(arrayOf(i + .2f, i + .3f, i + .4f, i + .5f, i + .6f, i + .7f))
        }

        Assertions.assertEquals(array, dataFromManual.toArray().toMutableList())
    }

    @Test
    fun testGestureDataToArrayZeroPadsMissingData() {
        val dataFromManual = GestureData()

        for (i in 1..GestureData.dataPointCount - 100) {
            dataFromManual.addTime(i + .1f)
            dataFromManual.addAccelerometerData(i + .2f, i + .3f, i + .4f)
            dataFromManual.addGyroscopeData(i + .5f, i + .6f, i + .7f)
        }

        val array = mutableListOf<Float>()
        for (i in 1..GestureData.dataPointCount) {
            if (i > GestureData.dataPointCount - 100) {
                array.addAll(arrayOf(0f, 0f, 0f, 0f, 0f, 0f))
            } else {
                array.addAll(arrayOf(i + .2f, i + .3f, i + .4f, i + .5f, i + .6f, i + .7f))
            }
        }

        Assertions.assertEquals(array, dataFromManual.toArray().toMutableList())
    }


}