package org.firstinspires.ftc.teamcode.systemsTest

import com.bylazar.configurables.annotations.Configurable
import com.bylazar.telemetry.PanelsTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.commonlibs.units.Time
import com.commonlibs.units.ms
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.library.TimeKeep
import org.firstinspires.ftc.teamcode.library.controller.PIDController
import org.firstinspires.ftc.teamcode.robot.Robot
import kotlin.math.absoluteValue

// Hold A to make the robot turn towards the pollen, drive to it and intake it.
// When the pollen is close (targetArea) the camera can't see it well anymore, so the robot
// drives straight for grabTimeMs to get it in the intake.
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
        // drive straight this fast and this long once the pollen is close
        @JvmField var grabPower = 0.4
        @JvmField var grabTimeMs = 1000.0
        // keep the last command for this long if the camera misses the pollen for a few frames
        @JvmField var lostTimeoutMs = 300.0
    }

    override fun runOpMode() {
        val panelsTelemetry = PanelsTelemetry.telemetry
        val robot = Robot(hardwareMap)
        val timeKeep = TimeKeep()

        robot.limelight.pollenPipeline()

        var grabUntil = 0.ms
        var lastSeen = 0.ms
        var lastForward = 0.0
        var lastTurn = 0.0

        waitForStart()

        while (opModeIsActive()) {
            robot.follower.update()
            timeKeep.resetDeltaTime()
            robot.limelight.update()
            val now = Time.now()

            var forward = 0.0
            var turn = 0.0
            var state = "manual"

            if (gamepad1.a) {
                if (now < grabUntil) {
                    forward = PollenChaseConfig.grabPower
                    state = "grabbing"
                }
                else if (robot.limelight.pollenVisible) {
                    lastSeen = now

                    turn = PollenChaseConfig.turnController
                        .calculate(robot.limelight.pollenTxFiltered, 0.0, timeKeep.deltaTime)
                        .coerceIn(-PollenChaseConfig.maxTurn, PollenChaseConfig.maxTurn)

                    if (robot.limelight.pollenTxFiltered.absoluteValue < PollenChaseConfig.txTolerance) {
                        forward = PollenChaseConfig.forwardController
                            .calculate(robot.limelight.pollenArea, PollenChaseConfig.targetArea, timeKeep.deltaTime)
                            .coerceIn(0.0, PollenChaseConfig.maxForward)
                    }

                    if (robot.limelight.pollenArea >= PollenChaseConfig.targetArea) {
                        grabUntil = now + PollenChaseConfig.grabTimeMs.ms
                        forward = PollenChaseConfig.grabPower
                        turn = 0.0
                        state = "grabbing"
                    }
                    else {
                        state = "chasing"
                    }

                    lastForward = forward
                    lastTurn = turn
                }
                else if (now - lastSeen < PollenChaseConfig.lostTimeoutMs.ms) {
                    forward = lastForward
                    turn = lastTurn
                    state = "lost"
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
                grabUntil = 0.ms

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