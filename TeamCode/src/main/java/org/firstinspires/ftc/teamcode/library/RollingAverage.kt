package org.firstinspires.ftc.teamcode.library

class RollingAverage(
    private val size: Int
) {
    private val array = DoubleArray(size)
    private var index = 0
    private var elements = 0

    fun add(elem: Double) {
        if (elements != 0) {
            index = (index + 1) % size
        }

        array[index] = elem

        if (elements < size) {
            elements++
        }
    }

    fun avg(): Double {
        if (elements == 0) return 0.0

        var total = 0.0
        var i = index
        repeat(elements) {
            total += array[i]
            i = (i - 1) % size
        }

        return total / elements
    }
}