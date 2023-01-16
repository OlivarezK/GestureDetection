package com.speakbyhand.app.core;

import android.speech.tts.TextToSpeech;

public class GestureCodeToPhraseConverter {
    TextToSpeech textToSpeech;

    public GestureCodeToPhraseConverter(TextToSpeech textToSpeech) {
        this.textToSpeech = textToSpeech;
    }

    public String toMappedPhrase(GestureCode gestureCode){
        switch (gestureCode){
            case Sample:
                return "Sample";
            case Toilet:
                return "I want to go to toilet";
            case EatFood:
                return "I want to eat food";
            case DrinkWater:
                return "I want to drink water";
            case Help:
                return "Help";
            case Yes:
                return "Yes";
            case No:
                return "No";
            case Unknown:
                return "";
        }
        return "";

    }
}