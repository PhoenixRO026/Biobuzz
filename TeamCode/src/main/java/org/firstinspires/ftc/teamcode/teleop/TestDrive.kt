package org.firstinspires.ftc.teamcode.teleop

import com.bylazar.configurables.annotations.Configurable
import com.bylazar.telemetry.PanelsTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.robot.Robot

@TeleOp(name = "TestDrive")
class TestDrive : LinearOpMode() {
    @Configurable
    object TestDriveConfig {
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

            if (gamepad1.right_bumper) {
                robot.intake.startIntake()
            }
            else if (gamepad1.left_bumper) {
                robot.intake.reverseIntake()
            }
            else {
                robot.intake.stopIntake()
            }

            if (gamepad1.right_trigger > 0.5) {
                robot.transfer.startTransfer()
            }
            else if (gamepad1.left_trigger > 0.5) {
                robot.transfer.reverseTransfer()
            }
            else if (gamepad1.a) {
                robot.transfer.slowTransfer()
            }
            else {
                robot.transfer.stopTransfer()
            }

            if (TestDriveConfig.telemetryDisabled) continue

            panelsTelemetry.addData("heading deg", Math.toDegrees(robot.drive.heading))
            panelsTelemetry.addData("intake power", robot.intake.power)
            panelsTelemetry.addData("transfer power", robot.transfer.power)
            panelsTelemetry.update(telemetry)
        }
    }
}