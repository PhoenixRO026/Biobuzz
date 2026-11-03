package org.firstinspires.ftc.teamcode.robot

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.commands.Commands.instant
import com.qualcomm.robotcore.hardware.DcMotorEx

class Intake(
    val motor : DcMotorEx
) {
    var power
        get() = motor.power
        set(value) {
            motor.power = value
        }

    fun startIntake() { power = 1.0 }
    fun stopIntake() { power = 0.0 }
    fun reverseIntake() { power = -1.0 }

    fun startIntakeCommand() : Command = instant { startIntake() }
    fun stopIntakeCommand() : Command = instant { stopIntake() }
    fun reverseIntakeCommand() : Command = instant { reverseIntake() }
}