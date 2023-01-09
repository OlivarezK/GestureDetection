package com.example.benchmarkapp.presentation.core.client

import com.example.benchmarkapp.presentation.core.GestureCode
import org.json.JSONArray
import org.json.JSONObject

class BenchmarkResult(val modelName: String) {
    val detectionInfos = listOf<GestureDetectionInfo>()

    fun toJsonString(): String {
        val jsonContent = JSONObject();
        jsonContent.put("modelName", modelName)
        jsonContent.put("detection", JSONArray(detectionInfos))
        return jsonContent.toString(4);
    }
}

class GestureDetectionInfo(
    val inputFileName: String,
    val modelResponseTimeMilliseconds: Number,
    val prediction: GestureCode
) {

}

