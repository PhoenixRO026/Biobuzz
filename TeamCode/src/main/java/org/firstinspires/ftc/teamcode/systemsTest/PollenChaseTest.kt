package org.firstinspires.ftc.teamcode.systemsTest

import com.bylazar.configurables.annotations.Configurable
import com.bylazar.telemetry.PanelsTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.library.TimeKeep
import org.firstinspires.ftc.teamcode.library.controller.PIDController
import org.firstinspires.ftc.teamcode.robot.Robot
import kotlin.math.absoluteValue

// Hold A to make the robot turn towards the pollen, drive to it and intake it.
// Release A to drive normally.
@TeleOp
class PollenChaseTest : LinearOpMode() {
    @Configurable
    object PollenChaseConfig {
        // turns so the pollen is in the middle of the image (tx = 0)
        @JvmField var turnController = PIDController(kP = 0.02)
        // drives forward until the pollen takes targetArea % of the image
        @JvmField var forwardController = PIDController(kP = 0.1)
        @JvmField var targetArea = 5.0
        @JvmField var maxTurn = 0.4
        @JvmField var maxForward = 0.5
        // only drives forward when the pollen is this close to the middle (deg)
        @JvmField var txTolerance = 5.0
        // turning speed while no pollen is seen, 0 to just wait
        @JvmField var searchTurn = 0.2
        @JvmField var runIntake = true
    }

    override fun runOpMode() {
        val panelsTelemetry = PanelsTelemetry.telemetry
        val robot = Robot(hardwareMap)
        val timeKeep = TimeKeep()

        robot.limelight.pollenPipeline()

        waitForStart()

        while (opModeIsActive()) {
            timeKeep.resetDeltaTime()
            robot.limelight.update()

            var forward = 0.0
            var turn = 0.0
            var state = "manual"

            if (gamepad1.a) {
                if (robot.limelight.pollenVisible) {
                    turn = PollenChaseConfig.turnController
                        .calculate(robot.limelight.pollenTxFiltered, 0.0, timeKeep.deltaTime)
                        .coerceIn(-PollenChaseConfig.maxTurn, PollenChaseConfig.maxTurn)

                    if (robot.limelight.pollenTxFiltered.absoluteValue < PollenChaseConfig.txTolerance) {
                        forward = PollenChaseConfig.forwardController
                            .calculate(robot.limelight.pollenArea, PollenChaseConfig.targetArea, timeKeep.deltaTime)
                            .coerceIn(0.0, PollenChaseConfig.maxForward)
                    }

                    if (robot.limelight.pollenArea >= PollenChaseConfig.targetArea) {
                        state = "at pollen"
                    }
                    else {
                        state = "chasing"
                    }
                }
                else {
                    turn = PollenChaseConfig.searchTurn
                    state = "searching"
                }

                robot.drive.driveRobotCentric(forward, 0.0, turn)

                if (PollenChaseConfig.runIntake) {
                    robot.intake.startIntake()
                }
                else {
                    robot.intake.stopIntake()
                }
            }
            else {
                robot.drive.driveRobotCentric(
                    -gamepad1.left_stick_y.toDouble(),
                    -gamepad1.left_stick_x.toDouble(),
                    -gamepad1.right_stick_x.toDouble()
                )
                robot.intake.power = gamepad1.right_trigger.toDouble() - gamepad1.left_trigger.toDouble()
            }

            panelsTelemetry.addData("state", state)
            panelsTelemetry.addData("limelight connected", robot.limelight.isConnected)
            panelsTelemetry.addData("pipeline", robot.limelight.pipelineIndex)
            panelsTelemetry.addData("pollen visible", robot.limelight.pollenVisible)
            panelsTelemetry.addData("pollen tx", robot.limelight.pollenTx)
            panelsTelemetry.addData("pollen tx filtered", robot.limelight.pollenTxFiltered)
            panelsTelemetry.addData("pollen ty", robot.limelight.pollenTy)
            panelsTelemetry.addData("pollen area", robot.limelight.pollenArea)
            panelsTelemetry.addData("forward", forward)
            panelsTelemetry.addData("turn", turn)
            panelsTelemetry.update(telemetry)
        }
    }
}