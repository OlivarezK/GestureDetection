package com.speakbyhand.app.core;

import android.content.Context;

import com.speakbyhand.app.ml.GestureConvModel;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;

public class GestureDetector {
    Context context;


    public GestureCode detect(GestureData data){
        try {
            GestureConvModel model = GestureConvModel.newInstance(context);

            TensorBuffer inputFeatures = TensorBuffer.createFixedSize(new int[]{1, 197, 6}, DataType.FLOAT32);
            inputFeatures.loadArray(data.toArray());

            GestureConvModel.Outputs outputs = model.process(inputFeatures);
            TensorBuffer outputFeatures = outputs.getOutputFeature0AsTensorBuffer();
            model.close();

            int predictionIndex = getArgMax(outputFeatures.getFloatArray());
            return toGestureCode(predictionIndex);
        } catch (IOException e) {
            // TODO Handle the exception
        }

        return GestureCode.DrinkWater;
    }

    // TODO: Create unit test for this method
    private int getArgMax(float[] floatArray) {
        int maxIndex = 0;
        for (int i = 0; i < floatArray.length; i++) {
            if(floatArray[i] > floatArray[maxIndex]){
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    // TODO: Create unit test for this method
    private GestureCode toGestureCode(int predictionIndex){
        switch (predictionIndex){
            case 0:
                return GestureCode.DrinkWater;
            case 1:
                return GestureCode.EatFood;
            case 2:
                return GestureCode.Help;
            case 3:
                return GestureCode.No;
            case 4:
                return GestureCode.Toilet;
            case 5:
                return GestureCode.Yes;
        }

        throw new IllegalArgumentException("Given argument: "+predictionIndex);
    }
}