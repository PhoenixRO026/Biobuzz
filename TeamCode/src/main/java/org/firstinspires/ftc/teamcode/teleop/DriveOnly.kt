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

@Config("Drive Only")
@TeleOp(name = "Drive Only")
class DriveOnly : LinearOpMode() {

    companion object {
        @JvmField var telemetryDisabled = false
    }

    override fun runOpMode() {
        val drive = Drive(hardwareMap)

        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)

        waitForStart()

        while (opModeIsActive()) {

            movement(drive)

            if (telemetryDisabled) continue

            drive.addTelemetry(telemetry)
            telemetry.update()
        }
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
}