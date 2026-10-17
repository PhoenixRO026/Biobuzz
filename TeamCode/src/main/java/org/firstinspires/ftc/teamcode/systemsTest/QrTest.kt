package org.firstinspires.ftc.teamcode.systemsTest

import com.bylazar.telemetry.PanelsTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.robot.Robot

@TeleOp
class QrTest : LinearOpMode() {
    override fun runOpMode() {
        val panelsTelemetry = PanelsTelemetry.telemetry
        val robot = Robot(hardwareMap)

        robot.limelight.qrPipeline()

        waitForStart()

        while (opModeIsActive()) {
            robot.limelight.update()

            robot.drive.driveRobotCentric(
                -gamepad1.left_stick_y.toDouble(),
                -gamepad1.left_stick_x.toDouble(),
                -gamepad1.right_stick_x.toDouble()
            )

            panelsTelemetry.addData("limelight connected", robot.limelight.isConnected)
            panelsTelemetry.addData("pipeline", robot.limelight.pipelineIndex)
            panelsTelemetry.addData("fresh result", robot.limelight.freshResult)
            panelsTelemetry.addData("qr codes seen", robot.limelight.qrCodes.size)
            robot.limelight.qrCodes.forEachIndexed { i, code ->
                panelsTelemetry.addData("qr $i", code)
            }
            panelsTelemetry.addData("last qr", robot.limelight.lastQrCode ?: "none")
            panelsTelemetry.update(telemetry)
        }
    }
}