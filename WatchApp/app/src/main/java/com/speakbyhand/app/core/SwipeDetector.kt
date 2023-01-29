package com.speakbyhand.app.core

import android.view.MotionEvent
import kotlin.math.abs

class SwipeDetector {
    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100
    var isSwiped = false
    val gestureDetector = android.view.GestureDetector(object: android.view.GestureDetector.OnGestureListener{
        override fun onDown(e: MotionEvent?): Boolean {
            return false
        }

        override fun onShowPress(e: MotionEvent?) {
            return
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return false
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            return false
        }

        override fun onLongPress(e: MotionEvent?) {
            return
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (abs(diffX) < abs(diffY)) {
                    if (abs(diffY) > swipeThreshold && abs(velocityY) > swipeVelocityThreshold) {
                        isSwiped = true
                    }
                }
            }
            catch (exception: Exception) {
                exception.printStackTrace()
            }
            return true
        }


    })
}