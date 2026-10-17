package org.firstinspires.ftc.teamcode.robot

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.follower.Follower
import kotlin.math.cos
import kotlin.math.sin

class Drive(
    private val follower: Follower
) {
    @Configurable
    object DriveConfig {
        @JvmField var slowSpeed = 0.5
    }

    var isSlowMode = false

    private var headingOffset = 0.0

    val heading: Double
        get() = follower.pose().heading() - headingOffset

    private val currentSpeed: Double
        get() = if (isSlowMode) DriveConfig.slowSpeed else 1.0

    fun resetFieldCentric() {
        headingOffset = follower.pose().heading()
    }

    fun driveFieldCentric(forward: Double, strafe: Double, rotate: Double) {
        val headingRad = -heading
        val rotatedForward = forward * cos(headingRad) - strafe * sin(headingRad)
        val rotatedStrafe = forward * sin(headingRad) + strafe * cos(headingRad)

        driveRobotCentric(rotatedForward, rotatedStrafe, rotate)
    }

    fun driveRobotCentric(forward: Double, strafe: Double, rotate: Double) {
        follower.manual(
            forward * currentSpeed,
            strafe * currentSpeed,
            rotate * currentSpeed
        )
    }
}