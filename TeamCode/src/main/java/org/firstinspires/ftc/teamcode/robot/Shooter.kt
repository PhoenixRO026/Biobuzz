package org.firstinspires.ftc.teamcode.robot

import com.bylazar.configurables.annotations.Configurable
import com.commonlibs.units.Duration
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.teamcode.library.controller.PIDController

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

    fun goToRpm(rpm : Double) {
        targetRpm = rpm
    }
}