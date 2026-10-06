package org.firstinspires.ftc.teamcode.teleop

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.robot.Drive
import org.firstinspires.ftc.teamcode.robot.Intake
import org.firstinspires.ftc.teamcode.robot.Transfer

@Config("TestDrive")
@TeleOp(name = "TestDrive")
class TestDrive : LinearOpMode() {

    companion object {
        @JvmField var telemetryDisabled = false
    }

    override fun runOpMode() {
        val drive = Drive(hardwareMap)
        val intake = Intake(hardwareMap)
        val transfer = Transfer(hardwareMap)

        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)

        waitForStart()

        while (opModeIsActive()) {

            movement(drive)
            intakeControl(intake)
            transferControl(transfer)

            if (telemetryDisabled) continue

            drive.addTelemetry(telemetry)
            intake.addTelemetry(telemetry)
            transfer.addTelemetry(telemetry)
            telemetry.update()
        }
    }

    fun movement(drive: Drive) {
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

    fun intakeControl(intake: Intake) {
        when {
            gamepad1.right_bumper -> intake.start()
            gamepad1.left_bumper -> intake.reverse()
            else -> intake.stop()
        }
    }

    fun transferControl(transfer: Transfer) {
        when {
            gamepad1.right_trigger > 0.5 -> transfer.start()
            gamepad1.left_trigger > 0.5 -> transfer.reverse()
            gamepad1.a -> transfer.slow()
            else -> transfer.stop()
        }
    }
}