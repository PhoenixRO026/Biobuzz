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

class Intake(
    private val motor: DcMotor,
) {
    constructor(hardwareMap: HardwareMap): this(
        hardwareMap.get(DcMotor::class.java, HardwareNames.motorIntake)
    )

    init {
        motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motor.direction = DcMotorSimple.Direction.REVERSE
    }

    var power by motor::power

    fun addTelemetry(telemetry: Telemetry) {
    }
}