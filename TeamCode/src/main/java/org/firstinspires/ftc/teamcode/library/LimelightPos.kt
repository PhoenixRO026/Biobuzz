package org.firstinspires.ftc.teamcode.library

import com.pedropathing.math.Pose
import kotlin.math.cos
import kotlin.math.sin

const val turretOffsetFromRobot = 66.994 / 25.5
const val limelightOffsetFromTurret = 159.601 / 25.4

fun limelightToRobotPos(limelightPose: Pose, turretHeadingDiff: Double): Pose {
    return limelightPose.toTurretPos(turretHeadingDiff).toRobotPose()
}

// turret to robot
fun Pose.toRobotPose(): Pose {
    // Rotate the turret offset by the robot's heading
    val offsetX = turretOffsetFromRobot * cos(this.heading())
    val offsetY = turretOffsetFromRobot * sin(this.heading())

    // Add offset to current pose
    return Pose(this.x() + offsetX, this.y() + offsetY, this.heading())
}

// limelight to turret

fun Pose.toTurretPos(turretHeadingDiff: Double): Pose {
    val offsetX = -limelightOffsetFromTurret * cos(this.heading())
    val offsetY = -limelightOffsetFromTurret * sin(this.heading())

    return Pose(
        this.x() + offsetX,
        this.y() + offsetY,
        this.heading() - turretHeadingDiff
    )
}