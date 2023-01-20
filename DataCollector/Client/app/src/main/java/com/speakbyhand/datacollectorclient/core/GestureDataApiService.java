package com.speakbyhand.datacollectorclient.core;
import static android.content.ContentValues.TAG;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import androidx.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

public class GestureDataApiService {
    // for API
    private final OkHttpClient client = new OkHttpClient();
    private int fileNum = 1;

    public void postData(String data){
        // API
        RequestBody formBody = new FormBody.Builder()
                .add("value", data)
                .add("fileNum", String.valueOf(fileNum))
                .build();
        Request request = new Request.Builder()
                .url("http://192.168.170.185:5000/post")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    ResponseBody responseBody = response.body();
                    Log.d(TAG, "onResponse: " + responseBody.string());

                    fileNum += 1;
                }
            }
        });
    }
}
