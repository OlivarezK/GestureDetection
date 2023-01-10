package com.speakbyhand.app.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LimitedTest {
    @Test
    void testAddOverflowDeletesBottomOfStack(){
        LimitedStack<Integer> stack = new LimitedStack<>(5);
        List<Integer> values = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
        List<Integer> expectedValues = Arrays.asList(6,7,8,9,10);

        values.forEach(stack::push);
        List<Integer> newValues = new ArrayList<>();
        for (int val: stack) {
            newValues.add(val);
        }

        assertEquals(expectedValues, newValues);
    }
}
