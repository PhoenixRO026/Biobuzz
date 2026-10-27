package org.firstinspires.ftc.teamcode.robot

import com.bylazar.configurables.annotations.Configurable
import com.commonlibs.units.Duration
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.commands.Commands.instant
import com.pedropathing.ivy.commands.Commands.waitUntil
import com.pedropathing.ivy.groups.Groups.sequential
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.teamcode.library.controller.PIDController
import kotlin.math.abs

class Shooter(
    val motorLeft : DcMotorEx,
    val motorRight: DcMotorEx,
    val voltageSensor : VoltageSensor
) {
    @Configurable
    object ShooterConfig {
        @JvmField
        var controllerRpm = PIDController(
            kP = 0.0001,
            kI = 0.0,
            kD = 0.0,
        )
        @JvmField var kS = 1.125
        @JvmField var kV = 0.002065
        @JvmField var targetRpmTolerance = 50.0
    }

    var targetRpm = 0.0
        set(value) {
            field = value.coerceAtLeast(0.0)
        }
    val currentRpm get() = -motorRight.velocity * 60.0 / 28

    var voltage = 12.0

    var shooterPower
        get() = motorRight.power
        set(value) {
            motorRight.power = value
            motorLeft.power = value
        }

    fun updateRpm(deltaTime : Duration) {
        val pidPower = ShooterConfig.controllerRpm.calculate(currentRpm, targetRpm, deltaTime)

        if (targetRpm == 0.0) {
            if (shooterPower != 0.0) {
                shooterPower = 0.0
            }
            return
        }

        voltage = voltageSensor.voltage
        val feedforwardPower = ShooterConfig.kS + ShooterConfig.kV * targetRpm
        shooterPower = pidPower + feedforwardPower / voltage
    }

    fun shooterBusy(): Boolean {
        return abs(targetRpm - currentRpm) > ShooterConfig.targetRpmTolerance
    }

    fun goToRpm(rpm : Double) {
        targetRpm = rpm
    }

    // updateRpm has to be called every loop, otherwise this never finishes
    fun goToRpmCommand(rpm: Double): Command =
        sequential(
            instant { goToRpm(rpm) },
            waitUntil { !shooterBusy() }
        )
}