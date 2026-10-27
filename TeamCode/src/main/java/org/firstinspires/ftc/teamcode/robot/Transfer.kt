package org.firstinspires.ftc.teamcode.robot

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.commands.Commands.instant
import com.qualcomm.robotcore.hardware.DcMotorEx

class Transfer(
    val motor: DcMotorEx
) {
    @Configurable
    object TransferConfig {
        @JvmField var transferPower = 1.0
        @JvmField var slowPower = 0.3
        @JvmField var reversePower = -1.0
    }

    var power
        get() = motor.power
        set(value) {
            motor.power = value
        }

    fun startTransfer() { power = TransferConfig.transferPower }
    fun stopTransfer() { power = 0.0 }
    fun reverseTransfer() { power = TransferConfig.reversePower }
    fun slowTransfer() { power = TransferConfig.slowPower }

    fun startTransferCommand() : Command = instant { startTransfer() }
    fun stopTransferCommand() : Command = instant { stopTransfer() }
    fun reverseTransferCommand() : Command = instant { reverseTransfer() }
    fun slowTransferCommand() : Command = instant { slowTransfer() }
}