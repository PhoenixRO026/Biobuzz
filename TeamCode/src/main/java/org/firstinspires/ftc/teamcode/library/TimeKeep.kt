package org.firstinspires.ftc.teamcode.library

import com.commonlibs.units.Time
import com.commonlibs.units.ms

class TimeKeep {
    private var isInitialized = false

    var previousTime = timeNow() - 1.ms
    var currentTime = timeNow()
    inline val deltaTime get() = currentTime - previousTime

    fun resetDeltaTime() {
        if (isInitialized.not()) {
            isInitialized = true
            currentTime = timeNow()
            previousTime = currentTime - 20.ms
            return
        }

        previousTime = currentTime
        currentTime = timeNow()
    }

    private fun timeNow() = Time.now()
}