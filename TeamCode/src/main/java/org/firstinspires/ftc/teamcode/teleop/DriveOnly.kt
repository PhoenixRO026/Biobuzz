package org.firstinspires.ftc.teamcode.teleop

import com.bylazar.configurables.annotations.Configurable
import com.bylazar.telemetry.PanelsTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.robot.Robot

@TeleOp(name = "Drive Only")
class DriveOnly : LinearOpMode() {
    @Configurable
    object DriveOnlyConfig {
        @JvmField var telemetryDisabled = false
    }

    override fun runOpMode() {
        val panelsTelemetry = PanelsTelemetry.telemetry
        val robot = Robot(hardwareMap)

        waitForStart()

        while (opModeIsActive()) {
            if (gamepad1.y) {
                robot.drive.resetFieldCentric()
            }

            robot.drive.isSlowMode = gamepad1.left_trigger_pressed

            robot.drive.driveFieldCentric(
                -gamepad1.left_stick_y.toDouble(),
                -gamepad1.left_stick_x.toDouble(),
                -gamepad1.right_stick_x.toDouble()
            )

            if (DriveOnlyConfig.telemetryDisabled) continue

            panelsTelemetry.addData("heading deg", Math.toDegrees(robot.drive.heading))
            panelsTelemetry.update(telemetry)
        }
    }
}