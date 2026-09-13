package org.firstinspires.ftc.teamcode.teleop

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.robot.Drive

@TeleOp(name = "Buzzy Drive")
class BuzzyDrive : LinearOpMode() {
    override fun runOpMode() {
        val drive = Drive(hardwareMap)

        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)

        waitForStart()

        while (opModeIsActive()) {
            movement(drive)
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