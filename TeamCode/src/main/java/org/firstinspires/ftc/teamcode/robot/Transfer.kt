package org.firstinspires.ftc.teamcode.robot

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.commands.Commands.instant
import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.DcMotorEx

class Transfer(
    val motor: DcMotorEx,
    val distanceSensor: AnalogInput
) {

    data object TransferConfig {
        val maxVoltage = 3.3
        val maxDistanceMm = 1000.0
    }

    var distance = 0.0
        get() =  voltageToDistanceMm(distanceSensor.voltage)

    fun voltageToDistanceMm(voltage: Double): Double {
        return (voltage / TransferConfig.maxVoltage) * TransferConfig.maxDistanceMm
    }

    fun isBallPresent(): Boolean {
        return distance in 0.0 .. 60.0
    }

    var power
        get() = motor.power
        set(value) {
            motor.power = value
        }

    fun startTransfer() { power = 1.0 }
    fun stopTransfer() { power = 0.0 }
    fun reverseTransfer() { power = -1.0 }
    fun slowTransfer() { power = 0.3 }

    fun startTransferCommand() : Command = instant { startTransfer() }
    fun stopTransferCommand() : Command = instant { stopTransfer() }
    fun reverseTransferCommand() : Command = instant { reverseTransfer() }
    fun slowTransferCommand() : Command = instant { slowTransfer() }
}