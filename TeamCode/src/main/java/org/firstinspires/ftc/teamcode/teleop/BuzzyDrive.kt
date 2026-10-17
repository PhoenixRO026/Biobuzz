package org.firstinspires.ftc.teamcode.teleop

import com.bylazar.configurables.annotations.Configurable
import com.bylazar.telemetry.PanelsTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.library.TimeKeep
import org.firstinspires.ftc.teamcode.library.buttons.ButtonReader
import org.firstinspires.ftc.teamcode.robot.Robot

@TeleOp(name = "Buzzy Drive")
class BuzzyDrive : LinearOpMode() {
    @Configurable
    object BuzzyConfig {
        @JvmField var telemetryDisabled = false
    }

    override fun runOpMode() {
        val panelsTelemetry = PanelsTelemetry.telemetry
        val robot = Robot(hardwareMap)

        val increaseRpm = ButtonReader { gamepad2.right_bumper }
        val decreaseRpm = ButtonReader { gamepad2.left_bumper }
        val kicker = ButtonReader { gamepad2.x }
        val buttons = listOf(increaseRpm, decreaseRpm, kicker)
        val timeKeep = TimeKeep()

        waitForStart()

        while (opModeIsActive()) {
            timeKeep.resetDeltaTime()
            buttons.forEach { it.readValue() }

            if (gamepad1.y) {
                robot.drive.resetFieldCentric()
            }

            robot.drive.isSlowMode = gamepad1.left_trigger_pressed

            robot.drive.driveFieldCentric(
                -gamepad1.left_stick_y.toDouble(),
                -gamepad1.left_stick_x.toDouble(),
                -gamepad1.right_stick_x.toDouble()
            )

            if (increaseRpm.wasJustPressed()) {
                robot.shooter.targetRpm += 500
            }
            if (decreaseRpm.wasJustPressed()) {
                robot.shooter.targetRpm -= 500
            }
            if (gamepad2.b) {
                robot.shooter.goToRpm(0.0)
            }

            robot.shooter.updateRpm(timeKeep.deltaTime)

            robot.transfer.power = -gamepad2.left_stick_y.toDouble()

//            if (gamepad2.dpad_up) {
//                robot.transfer.kickerPos += 0.33 * timeKeep.deltaTime.asS
//            } else if (gamepad2.dpad_down) {
//                robot.transfer.kickerPos -= 0.33 * timeKeep.deltaTime.asS
//            }
//
//            if (kicker.wasJustPressed()) {
//                robot.transfer.kickerUp()
//            }
//            if (kicker.wasJustReleased()) {
//                robot.transfer.kickerDown()
//            }

            robot.intake.power = gamepad2.right_trigger.toDouble() - gamepad2.left_trigger.toDouble()

            if (BuzzyConfig.telemetryDisabled) continue

            panelsTelemetry.addData("target rpm", robot.shooter.targetRpm)
            panelsTelemetry.addData("current rpm", robot.shooter.currentRpm)
            panelsTelemetry.addData("shooter power", robot.shooter.shooterPower)
            panelsTelemetry.addData("voltage", robot.shooter.voltage)
            panelsTelemetry.addData("heading deg", Math.toDegrees(robot.drive.heading))
            panelsTelemetry.addData("intake power", robot.intake.power)
            panelsTelemetry.addData("transfer power", robot.transfer.power)
            panelsTelemetry.update(telemetry)
        }
    }
}