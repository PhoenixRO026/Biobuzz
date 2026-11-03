package org.firstinspires.ftc.teamcode.robot

import com.bylazar.configurables.annotations.Configurable
import com.commonlibs.units.Duration
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.commands.Commands.instant
import com.pedropathing.ivy.commands.Commands.waitUntil
import com.pedropathing.ivy.groups.Groups.sequential
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.teamcode.library.controller.PIDController
import kotlin.math.abs
import kotlin.math.pow

class Shooter(
    val motorRhino : DcMotorEx,
    val motorGecko: DcMotorEx,
    val servo1 : Servo,
    val servo2 : Servo,
    val finger : Servo,
    val voltageSensor : VoltageSensor,
    val motorEncoder : DcMotorEx
) {
    @Configurable
    object ShooterConfig {
        @JvmField
        var controller_Rhino = PIDController(
            kP = 0.0015,
            kD = 0.00001,
            kI = 0.00,
            stabilityThreshold = 50.0
        )
        @JvmField var kS_Rhino = 1.55
        @JvmField var kV_Rhino = 0.002267
        @JvmField var targetRpm_Rhino = 1000.0

        @JvmField
        var controller_Gecko = PIDController(
            kP = 0.0005,
            kD = 0.00001,
            kI = 0.00,
            stabilityThreshold = 50.0
        )
        @JvmField var kS_Gecko = 1.11
        @JvmField var kV_Gecko = 0.00242
        @JvmField var targetRpm_Gecko = 1000.0
        @JvmField var targetRpmTolerance = 50.0

        @JvmField var maxFinalDegrees = 360.0
    }

    // turret stuff
    fun servoToDeg(servoPos: Double): Double {
        return (servoPos - 0.5) * ShooterConfig.maxFinalDegrees
    }

    fun degToServo(deg: Double): Double {
        return deg / ShooterConfig.maxFinalDegrees + 0.5
    }

    var turretPosition = 0.5
        get() = servo1.position
        set(value) {
            field = value.coerceIn(0.175, 0.850)
            servo1.position = field
            servo2.position = field
        }

    var turretAngle get() = -servoToDeg(turretPosition)
        set(value) {
            turretPosition = degToServo(value)
        }

    fun turretToPosition(pos : Double) : Command = instant { turretPosition = pos }
    fun turretGoToAngle(targetAngle: Double) { turretAngle = targetAngle }
    fun updateHeading(headingError : Double) {
        turretPosition += degToServo(headingError)
    }

    //finger stuff

    var fingerPosition
        get() = finger.position
        set(value) {
            finger.position = value
        }

    fun openFinger() { fingerPosition = 0.5 }
    fun closeFinger() { fingerPosition = 0.5 }

    fun openFingerCommand() : Command = instant { openFinger() }
    fun closeFingerCommand() : Command = instant { closeFinger() }

    //rpm stuff
    var targetRpm_Rhino = 0.0
        set(value) {
            field = value.coerceAtLeast(0.0)
        }
    var targetRpm_Gecko = 0.0
        set(value) {
            field = value.coerceAtLeast(0.0)
        }

    var autoRpm_Rhino = 0.0
    var autoRpm_Gecko = 0.0
    val currentRpm_Rhino get() = -motorRhino.velocity * 60.0 / 28
    val currentRpm_Gecko get() = -motorGecko.velocity * 60.0 / 28

    var voltage = 12.0

    var motorPower_Rhino
        get() = motorRhino.power
        set(value) {
            motorRhino.power = value
        }
    var motorPower_Gecko
        get() = motorGecko.power
        set(value) {
            motorGecko.power = value
        }

    fun updateRpm_Rhino(deltaTime : Duration) {
        val pidPower_Rhino = ShooterConfig.controller_Rhino.calculate(currentRpm_Rhino, targetRpm_Rhino, deltaTime)

        if (targetRpm_Rhino == 0.0) {
            if (motorPower_Rhino != 0.0) {
                motorPower_Rhino = 0.0
            }
            return
        }

        voltage = voltageSensor.voltage
        val feedforwardPower = ShooterConfig.kS_Rhino + ShooterConfig.kV_Rhino * targetRpm_Rhino
        motorPower_Rhino = pidPower_Rhino + feedforwardPower / voltage
    }

    fun updateRpm_Gecko(deltaTime : Duration) {
        val pidPower_Gecko = ShooterConfig.controller_Gecko.calculate(currentRpm_Gecko, targetRpm_Gecko, deltaTime)

        if (targetRpm_Gecko == 0.0) {
            if (motorPower_Gecko != 0.0) {
                motorPower_Gecko = 0.0
            }
            return
        }

        voltage = voltageSensor.voltage
        val feedforwardPower = ShooterConfig.kS_Gecko + ShooterConfig.kV_Gecko * targetRpm_Gecko
        motorPower_Rhino = pidPower_Gecko + feedforwardPower / voltage
    }

    fun updateRpm(deltaTime : Duration) {
        updateRpm_Rhino(deltaTime)
        updateRpm_Gecko(deltaTime)
    }

    fun shooterBusy_Rhino(): Boolean {
        return abs(targetRpm_Rhino - currentRpm_Rhino) > ShooterConfig.targetRpmTolerance
    }
    fun shooterBusy_Gecko(): Boolean {
        return abs(targetRpm_Gecko - currentRpm_Gecko) > ShooterConfig.targetRpmTolerance
    }

    fun goToRpm(rpm_Rhino : Double = 0.0, rpm_Gecko : Double = 0.0) {
        targetRpm_Rhino = rpm_Rhino
        targetRpm_Gecko = rpm_Gecko
    }

    // updateRpm has to be called every loop, otherwise this never finishes


    fun goToRpmCommand(rpm_Rhino : Double = 0.0, rpm_Gecko : Double = 0.0) : Command =
        sequential(
            instant { goToRpm(rpm_Rhino, rpm_Gecko) },
            waitUntil { !shooterBusy_Rhino() && !shooterBusy_Gecko() }
        )

    fun goToAutoRpmCommand(): Command =
        sequential(
            instant { goToRpm(autoRpm_Rhino, autoRpm_Gecko) },
            waitUntil { !shooterBusy_Rhino() && !shooterBusy_Gecko() }
        )

    //not actually calculated yet
    fun neededRpm_Rhino(distance: Double) : Double {
        autoRpm_Rhino = -0.0208476 * distance.pow(2) +
                11.85304 * distance +
                2053.49282
        return autoRpm_Rhino.coerceIn(0.0, 6000.0)
    }
    fun neededRpm_Gecko(distance: Double) : Double {
        autoRpm_Gecko = -0.0208476 * distance.pow(2) +
                11.85304 * distance +
                2053.49282
        return autoRpm_Gecko.coerceIn(0.0, 6000.0)
    }
}
