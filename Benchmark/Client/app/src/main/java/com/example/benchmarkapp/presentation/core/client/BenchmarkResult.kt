package com.example.benchmarkapp.presentation.core.client

import com.example.benchmarkapp.presentation.core.GestureCode
import org.json.JSONArray

class BenchmarkResult {
    val detectionInfos = listOf<GestureDetectionInfo>()

    fun toJsonString(): String {
        return JSONArray(detectionInfos).toString(4);
    }
}


class GestureDetectionInfo(
    val inputFileName: String,
    val modelResponseTimeMilliseconds: Number,
    val prediction: GestureCode
) {

}

