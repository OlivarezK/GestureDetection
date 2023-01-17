package com.example.benchmarkapp.presentation.core.client

import com.example.benchmarkapp.presentation.core.GestureCode
import org.json.JSONArray
import org.json.JSONObject

class BenchmarkResult(val modelName: String) {
    private val detectionInfos = mutableListOf<GestureDetectionInfo>()

    fun add(
        inputFileName: String,
        modelResponseTimeMilliseconds: Number,
        prediction: GestureCode
    ) {
        detectionInfos.add(
            GestureDetectionInfo(
                inputFileName,
                modelResponseTimeMilliseconds,
                prediction
            )
        )
    }

    fun toJson(): JSONObject {
        val jsonContent = JSONObject();
        jsonContent.put("modelName", modelName)

        val detectionInfoJsonArray = JSONArray()
        detectionInfos.forEach {
            val detectionInfoObject = JSONObject()
            detectionInfoObject.put("filename", it.inputFileName)
            detectionInfoObject.put("responseTimeMs", it.modelResponseTimeMilliseconds)
            detectionInfoObject.put("prediction", it.prediction)
            detectionInfoJsonArray.put(detectionInfoObject)
        }
        jsonContent.put("detection", detectionInfoJsonArray)
        return jsonContent;
    }
}

class GestureDetectionInfo(
    val inputFileName: String,
    val modelResponseTimeMilliseconds: Number,
    val prediction: GestureCode
) {

}

