package org.firstinspires.ftc.teamcode.robot

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry

@Config("Transfer")
class Transfer(
    private val motor: DcMotor
) {
    constructor(hardwareMap: HardwareMap): this(
        hardwareMap.get(DcMotor::class.java, HardwareNames.motorTransfer)
    )

    companion object {
        @JvmField var transferPower = 1.0
        @JvmField var slowPower = 0.3
        @JvmField var reversePower = -1.0
    }

    init {
        motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motor.direction = DcMotorSimple.Direction.REVERSE
        motor.power = 0.0
    }

    var power by motor::power

    fun start() {
        power = transferPower
    }

    fun slow() {
        power = slowPower
    }

    fun reverse() {
        power = reversePower
    }

    fun stop() {
        power = 0.0
    }

    fun addTelemetry(telemetry: Telemetry) {
        telemetry.apply {
            addData("transfer power", power)
        }
    }
}