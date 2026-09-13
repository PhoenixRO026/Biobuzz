package org.firstinspires.ftc.teamcode.robot

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

class Drive(
    private val leftFront: DcMotor,
    private val leftBack: DcMotor,
    private val rightFront: DcMotor,
    private val rightBack: DcMotor,
    private val pinpoint: GoBildaPinpointDriver
) {
    constructor(hardwareMap: HardwareMap): this(
        hardwareMap.get(DcMotor::class.java, HardwareNames.motorLeftFront),
        hardwareMap.get(DcMotor::class.java, HardwareNames.motorLeftBack),
        hardwareMap.get(DcMotor::class.java, HardwareNames.motorRightFront),
        hardwareMap.get(DcMotor::class.java, HardwareNames.motorRightBack),
        hardwareMap.get(GoBildaPinpointDriver::class.java, HardwareNames.pinpoint)
    )

    @Config("Drive")
    companion object {
        @JvmField var slowSpeed = 0.5
        @JvmField var xOffset = 0.0
        @JvmField var yOffset = 0.0
        @JvmField var xDirection = GoBildaPinpointDriver.EncoderDirection.FORWARD
        @JvmField var yDirection = GoBildaPinpointDriver.EncoderDirection.FORWARD
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

        pinpoint.setOffsets(xOffset, yOffset, DistanceUnit.MM)
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
        pinpoint.setEncoderDirections(xDirection, yDirection)
        pinpoint.resetPosAndIMU()
    }

    private var headingOffset = 0.0

    private val heading get() = getHeadingRad() - headingOffset

    fun resetFieldCentric() {
        headingOffset = getHeadingRad()
    }

    fun updatePinpoint() {
        pinpoint.update()
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

    private fun getHeadingRad() = pinpoint.getHeading(AngleUnit.RADIANS)

    fun addTelemetry(telemetry: Telemetry) {
        telemetry.apply {
            addData("heading deg", Math.toDegrees(heading))
            addData("pinpoint heading deg", Math.toDegrees(getHeadingRad()))
        }
    }
}