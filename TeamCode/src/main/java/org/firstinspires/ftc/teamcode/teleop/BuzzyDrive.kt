package org.firstinspires.ftc.teamcode.teleop

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.commonlibs.units.s
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.library.RollingAverage
import org.firstinspires.ftc.teamcode.library.TimeKeep
import org.firstinspires.ftc.teamcode.library.buttons.ButtonReader
import org.firstinspires.ftc.teamcode.robot.Drive
import org.firstinspires.ftc.teamcode.robot.Intake
import org.firstinspires.ftc.teamcode.robot.Shooter
import org.firstinspires.ftc.teamcode.robot.Transfer

@TeleOp(name = "Buzzy Drive")
class BuzzyDrive : LinearOpMode() {
    @Config("Buzzy")
    companion object {
        @JvmField var telemetryDisabled = false
    }

    private val timeKeep = TimeKeep()
    private val loopMsAvg = RollingAverage(10)
    private val fpsAvg = RollingAverage(10)
    private val increaseRpm = ButtonReader { gamepad2.right_bumper }
    private val decreaseRpm = ButtonReader { gamepad2.left_bumper }
    private val buttons = listOf(increaseRpm, decreaseRpm)

    override fun runOpMode() {
        val drive = Drive(hardwareMap)
        val intake = Intake(hardwareMap)
        val transfer = Transfer(hardwareMap)
        val shooter = Shooter(hardwareMap)

        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)

        waitForStart()

        transfer.initPos()

        while (opModeIsActive()) {
            timeKeep.resetDeltaTime()
            buttons.forEach { it.readValue() }

            movement(drive)

            shooter(shooter)

            transfer(transfer)

            intake.power = gamepad2.right_trigger.toDouble() - gamepad2.left_trigger.toDouble()

            if (telemetryDisabled) continue

            addStatistics()
            shooter.addTelemetry(telemetry)
            drive.addTelemetry(telemetry)
            intake.addTelemetry(telemetry)
            transfer.addTelemetry(telemetry)
            telemetry.update()
        }
    }

    fun shooter(shooter: Shooter) {
        if (increaseRpm.wasJustPressed()) {
            shooter.targetRpm += 500
        }

        if (decreaseRpm.wasJustPressed()) {
            shooter.targetRpm -= 500
        }

        if (gamepad2.b) {
            shooter.targetRpm = 0.0
        }

        shooter.update(timeKeep.deltaTime)
    }

    fun transfer(transfer: Transfer) {
        transfer.power = - gamepad2.left_stick_y.toDouble()

        if (gamepad2.dpad_up) {
            transfer.kickerPos += 0.33 * timeKeep.deltaTime.asS
        } else if (gamepad2.dpad_down) {
            transfer.kickerPos -= 0.33 * timeKeep.deltaTime.asS
        }

        if (gamepad2.x) {
            transfer.kickerUp()
        }

        transfer.update()
    }

    fun movement(drive: Drive) {
        drive.updatePinpoint()

        if (gamepad1.y) {
            drive.resetFieldCentric()
        }

        drive.slowMode = gamepad1.left_trigger_pressed

        drive.driveFieldCentric(
            forward = -gamepad1.left_stick_y.toDouble(),
            strafe = -gamepad1.left_stick_x.toDouble(),
            rotate = -gamepad1.right_stick_x.toDouble()
        )
    }

    fun addStatistics() {
        loopMsAvg.add(timeKeep.deltaTime.asMs)
        fpsAvg.add(1.s / timeKeep.deltaTime)

        telemetry.addData("loop time ms", loopMsAvg.avg())
        telemetry.addData("fps", fpsAvg.avg())
    }
}