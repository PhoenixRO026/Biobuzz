package org.firstinspires.ftc.teamcode.robot

import com.bylazar.configurables.annotations.Configurable
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.IMU
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

class ImuDrive(
    val motorLF : DcMotorEx,
    val motorLB : DcMotorEx,
    val motorRB : DcMotorEx,
    val motorRF : DcMotorEx,
    val imu : IMU
) {
    @Configurable
    object ImuDriveConfig {
        @JvmField var slowSpeed = 0.5
        @JvmField var logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.BACKWARD
        @JvmField var usbDirection = RevHubOrientationOnRobot.UsbFacingDirection.UP
    }

    var isSlowMode = false

    val heading: Double
        get() = imu.robotYawPitchRollAngles.getYaw(AngleUnit.RADIANS)

    private val currentSpeed: Double
        get() = if (isSlowMode) ImuDriveConfig.slowSpeed else 1.0

    fun resetFieldCentric() {
        imu.resetYaw()
    }

    fun driveFieldCentric(forward: Double, strafe: Double, rotate: Double) {
        val headingRad = -heading
        val rotatedForward = forward * cos(headingRad) - strafe * sin(headingRad)
        val rotatedStrafe = forward * sin(headingRad) + strafe * cos(headingRad)

        driveRobotCentric(rotatedForward, rotatedStrafe, rotate)
    }

    fun driveRobotCentric(forward: Double, strafe: Double, rotate: Double) {
        val x = forward * currentSpeed
        val y = strafe * currentSpeed
        val turn = rotate * currentSpeed

        val powerLF = x - y - turn
        val powerLB = x + y - turn
        val powerRB = x - y + turn
        val powerRF = x + y + turn

        val maxPower =
            listOf(1.0, powerLF, powerLB, powerRB, powerRF)
                .maxOf { it.absoluteValue }

        motorLF.power = powerLF / maxPower
        motorLB.power = powerLB / maxPower
        motorRF.power = powerRF / maxPower
        motorRB.power = powerRB / maxPower
    }
}