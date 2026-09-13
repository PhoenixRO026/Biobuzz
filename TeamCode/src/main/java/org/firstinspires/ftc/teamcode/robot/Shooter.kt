package org.firstinspires.ftc.teamcode.robot

import com.acmerobotics.dashboard.config.Config
import com.commonlibs.units.Duration
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.library.controller.PIDController
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

class Shooter(
    private val motorLeft: DcMotorEx,
    private val motorRight: DcMotorEx,
    private val voltageSensor: VoltageSensor
) {
    constructor(hardwareMap: HardwareMap): this(
        hardwareMap.get(DcMotorEx::class.java, HardwareNames.motorShooterLeft),
        hardwareMap.get(DcMotorEx::class.java, HardwareNames.motorShooterRight),
        hardwareMap.get(VoltageSensor::class.java, HardwareNames.voltage)
    )

    @Config("Shooter")
    companion object {
        @JvmField var targetRpmShooter = 0.0
        @JvmField var controller = PIDController(
            kP = 0.0,
            kI = 0.0,
            kD = 0.0,
        )
        @JvmField var kS = 0.0
        @JvmField var kV = 0.0
    }

    init {
        motorLeft.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        motorRight.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        motorLeft.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motorRight.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motorLeft.direction = DcMotorSimple.Direction.REVERSE
        motorRight.direction = DcMotorSimple.Direction.FORWARD
    }

    private var power
        get() = motorRight.power
        set(value) {
            motorRight.power = value
            motorLeft.power = value
        }

    val motorPower get() = power

    val currentRpm get() = motorRight.velocity * 60.0 / 28

    var targetRpm
        get() = targetRpmShooter
        set(value) {
            targetRpmShooter = value.coerceAtLeast(0.0)
        }

    private var voltage = 12.0

    fun update(deltaTime: Duration) {
        val pidPower = controller.calculate(currentRpm, targetRpm, deltaTime)

        if (targetRpm == 0.0) {
            if (power != 0.0) {
                power = 0.0
            }

            return
        }

        voltage = voltageSensor.voltage
        val feedForward = kS + kV * targetRpm
        power = feedForward / voltage + pidPower
    }

    fun addTelemetry(telemetry: Telemetry) {
        telemetry.apply {
            addData("target rpm", targetRpm)
            addData("current rpm", currentRpm)
            addData("shooter power", motorPower)
            addData("voltage", voltage)
        }
    }
}