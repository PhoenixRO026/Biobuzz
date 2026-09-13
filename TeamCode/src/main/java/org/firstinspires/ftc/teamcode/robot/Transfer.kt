package org.firstinspires.ftc.teamcode.robot

import com.acmerobotics.dashboard.config.Config
import com.commonlibs.units.Time
import com.commonlibs.units.s
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

class Transfer(
    private val motor: DcMotor,
    private val servo: Servo
) {
    constructor(hardwareMap: HardwareMap): this(
        hardwareMap.get(DcMotor::class.java, HardwareNames.motorTransfer),
        hardwareMap.get(Servo::class.java, HardwareNames.servoKicker)
    )

    @Config("Transfer")
    companion object {
        @JvmField var servoMin = 1.0
        @JvmField var servoMax = 0.75
        @JvmField var servoInit = 1.0
    }

    init {
        motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motor.direction = DcMotorSimple.Direction.REVERSE
    }

    fun initPos() {
        kickerPos = servoInit
    }

    var power by motor::power

    var kickerPos by servo::position

    fun kickerUp() {
        kickerPos = servoMax
    }

    fun kickerDown() {
        kickerPos = servoMin
    }

    fun addTelemetry(telemetry: Telemetry) {
        telemetry.apply {
            addData("finger pos", kickerPos)
        }
    }
}