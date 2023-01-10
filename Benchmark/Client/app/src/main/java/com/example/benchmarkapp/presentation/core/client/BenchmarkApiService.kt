package com.example.benchmarkapp.presentation.core.client


import android.content.ContentValues.TAG
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import android.util.Log;
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class BenchmarkApiService {
    private val client: OkHttpClient = OkHttpClient()

    /*fun postData(result: BenchmarkResult) {
        val endpoint = "http:/192.168.100.7:5000/benchmark"
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val formBody: RequestBody = result.toJsonString().toRequestBody(mediaType)
        val request: Request = Request.Builder()
            .url(endpoint)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: okio.IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseBody: ResponseBody = response.body
                        Log.d(TAG, "onResponse: " + responseBody.string())
                    }
                }
            }
        )
    }*/

}