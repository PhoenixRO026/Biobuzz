package org.firstinspires.ftc.teamcode.robot

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.IMU
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

@Config("Drive")
class Drive(
    private val leftFront: DcMotor,
    private val leftBack: DcMotor,
    private val rightFront: DcMotor,
    private val rightBack: DcMotor,
    private val imu: IMU
) {
    constructor(hardwareMap: HardwareMap): this(
        hardwareMap.get(DcMotor::class.java, HardwareNames.motorLeftFront),
        hardwareMap.get(DcMotor::class.java, HardwareNames.motorLeftBack),
        hardwareMap.get(DcMotor::class.java, HardwareNames.motorRightFront),
        hardwareMap.get(DcMotor::class.java, HardwareNames.motorRightBack),
        hardwareMap.get(IMU::class.java, HardwareNames.imu)
    )

    companion object {
        @JvmField var slowSpeed = 0.5
        @JvmField var logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.BACKWARD
        @JvmField var usbDirection = RevHubOrientationOnRobot.UsbFacingDirection.UP
    }

    init {
        val motors = listOf(leftFront, leftBack, rightFront, rightBack)
        motors.forEach {
            it.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
            it.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        }
        rightFront.direction = DcMotorSimple.Direction.REVERSE
        rightBack.direction = DcMotorSimple.Direction.REVERSE
        leftFront.direction = DcMotorSimple.Direction.FORWARD
        leftBack.direction = DcMotorSimple.Direction.FORWARD

        imu.initialize(
            IMU.Parameters(
                RevHubOrientationOnRobot(logoDirection, usbDirection)
            )
        )
        imu.resetYaw()
    }

    private val heading get() = getHeadingRad()

    fun resetFieldCentric() {
        imu.resetYaw()
    }

    var slowMode = false

    fun driveFieldCentric(forward: Double, strafe: Double, rotate: Double) {
        val headingRad = - heading
        val rotatedForward   = forward * cos(headingRad) - strafe * sin(headingRad)
        val rotatedStrafe    = forward * sin(headingRad) + strafe * cos(headingRad)

        driveRobotCentric(rotatedForward, rotatedStrafe, rotate)
    }

    fun driveRobotCentric(forward: Double, strafe: Double, rotate: Double) {
        val driveSpeed = if (slowMode) slowSpeed else 1.0
        val forwardScaled = forward * driveSpeed
        val strafeScaled = strafe * driveSpeed
        val rotateScaled = rotate * driveSpeed

        val powerLF = forwardScaled - strafeScaled - rotateScaled
        val powerLB = forwardScaled + strafeScaled - rotateScaled
        val powerRB = forwardScaled - strafeScaled + rotateScaled
        val powerRF = forwardScaled + strafeScaled + rotateScaled

        val maxPower = listOf(1.0, powerLF, powerLB, powerRB, powerRF)
            .maxOf { it.absoluteValue }

        leftFront   .power = powerLF / maxPower
        leftBack    .power = powerLB / maxPower
        rightFront  .power = powerRF / maxPower
        rightBack   .power = powerRB / maxPower
    }

    private fun getHeadingRad() = imu.robotYawPitchRollAngles.getYaw(AngleUnit.RADIANS)

    fun addTelemetry(telemetry: Telemetry) {
        telemetry.apply {
            addData("heading deg", Math.toDegrees(heading))
        }
    }
}