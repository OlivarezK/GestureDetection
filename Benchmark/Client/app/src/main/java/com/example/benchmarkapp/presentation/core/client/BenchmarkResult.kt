package com.example.benchmarkapp.presentation.core.client

import com.example.benchmarkapp.presentation.core.GestureCode
import org.json.JSONArray
import org.json.JSONObject

class BenchmarkResult(val modelName: String) {
    val detectionInfos = listOf<GestureDetectionInfo>()

    fun toJson(): JSONObject {
        val jsonContent = JSONObject();
        jsonContent.put("modelName", modelName)
        jsonContent.put("detection", JSONArray(detectionInfos))
        return jsonContent;
    }
}

class GestureDetectionInfo(
    val inputFileName: String,
    val modelResponseTimeMilliseconds: Number,
    val prediction: GestureCode
) {

}

