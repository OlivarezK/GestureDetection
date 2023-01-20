package com.speakbyhand.app.core

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*
import java.util.function.Consumer

class LimitedDequeTest {
    @Test
    fun tesTopReturnsLatestAddedItem() {
        val stack = LimitedDeque<Int>(4)
        stack.push(1)
        Assertions.assertEquals(1, stack.top())
        stack.push(2)
        Assertions.assertEquals(2, stack.top())
        stack.push(3)
        Assertions.assertEquals(3, stack.top())
        stack.push(4)
        Assertions.assertEquals(4, stack.top())
        stack.push(5)
        Assertions.assertEquals(5, stack.top())
        stack.push(6)
        Assertions.assertEquals(6, stack.top())
        stack.push(7)
        Assertions.assertEquals(7, stack.top())
        stack.push(8)
        Assertions.assertEquals(8, stack.top())
        stack.push(9)
        Assertions.assertEquals(9, stack.top())
    }

    @Test
    fun testBottomReturnsProperItem() {
        val stack = LimitedDeque<Int>(4)
        stack.push(1)
        Assertions.assertEquals(1, stack.bottom())
        stack.push(2)
        Assertions.assertEquals(1, stack.bottom())
        stack.push(3)
        Assertions.assertEquals(1, stack.bottom())
        stack.push(4)
        Assertions.assertEquals(1, stack.bottom())
        stack.push(5)
        Assertions.assertEquals(2, stack.bottom())
        stack.push(6)
        Assertions.assertEquals(3, stack.bottom())
        stack.push(7)
        Assertions.assertEquals(4, stack.bottom())
        stack.push(8)
        Assertions.assertEquals(5, stack.bottom())
        stack.push(9)
        Assertions.assertEquals(6, stack.bottom())
    }

    @Test
    fun testAddOverflowDeletesBottomOfStack() {
        val stack = LimitedDeque<Int>(5)
        val values = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        val expectedValues = listOf(6, 7, 8, 9, 10)
        values.forEach(Consumer { item: Int -> stack.push(item) })
        val newValues = stack.forwardIterator.asSequence().toList()
        Assertions.assertEquals(expectedValues, newValues)
    }
}