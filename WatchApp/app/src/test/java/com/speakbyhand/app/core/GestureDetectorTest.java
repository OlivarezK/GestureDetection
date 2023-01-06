package com.speakbyhand.app.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.util.Objects;
import java.util.Scanner;

class GestureDetectorTest {
    private final GestureDetector detector = new GestureDetector();

    @Test
    void testToiletGestureCanBeDetected(){
        String input = loadResourceAsString("/Toilet_1");
        GestureData data = new GestureData(input);

        GestureCode result = detector.detect(data);

        assertEquals(result, GestureCode.Toilet);
    }

    @Test
    void testEatFoodGestureCanBeDetected(){
        String input = loadResourceAsString("/Eat_1");
        GestureData data = new GestureData(input);

        GestureCode result = detector.detect(data);

        assertEquals(result, GestureCode.EatFood);
    }

    @Test
    void testDrinkWaterGestureCanBeDetected(){
        String input = loadResourceAsString("/Drink_1");
        GestureData data = new GestureData(input);

        GestureCode result = detector.detect(data);

        assertEquals(result, GestureCode.DrinkWater);
    }

    @Test
    void testHelpGestureCanBeDetected(){
        String input = loadResourceAsString("/Help_1");
        GestureData data = new GestureData(input);

        GestureCode result = detector.detect(data);

        assertEquals(result, GestureCode.Help);
    }

    @Test
    void testYesGestureCanBeDetected(){
        String input = loadResourceAsString("/Yes_1");
        GestureData data = new GestureData(input);

        GestureCode result = detector.detect(data);

        assertEquals(result, GestureCode.Yes);
    }

    @Test
    void testNoGestureCanBeDetected(){
        String input = loadResourceAsString("/No_2");
        GestureData data = new GestureData(input);

        GestureCode result = detector.detect(data);

        assertEquals(result, GestureCode.No);
    }

    public String loadResourceAsString(String fileName) {
        Scanner scanner = new Scanner(getClass().getResourceAsStream(fileName));
        String contents = scanner.useDelimiter("\\A").next();
        scanner.close();
        return contents;
    }
}