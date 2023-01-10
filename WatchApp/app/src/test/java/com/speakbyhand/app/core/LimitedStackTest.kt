package com.speakbyhand.app.core;

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LimitedStackTest {
    @Test
    fun testAddOverflowDeletesBottomOfStack(){
        val stack = LimitedStack<Number>(5);
        val values = listOf(1,2,3,4,5,6,7,8,9,10)
        val expectedValues = listOf(6,7,8,9,10)

        values.forEach { stack.push(it) }
        val newValues = stack.iterator().asSequence().toList()

        assertEquals(expectedValues, newValues)
    }
}