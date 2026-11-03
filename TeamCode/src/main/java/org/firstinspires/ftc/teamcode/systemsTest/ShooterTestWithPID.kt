package org.firstinspires.ftc.teamcode.systemsTest

import com.bylazar.configurables.annotations.Configurable
import com.bylazar.telemetry.PanelsTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.library.TimeKeep
import org.firstinspires.ftc.teamcode.robot.Robot
import org.firstinspires.ftc.teamcode.robot.Shooter
import org.firstinspires.ftc.teamcode.systemsTest.ShooterTestWithPID.TestConfig.power1
import org.firstinspires.ftc.teamcode.systemsTest.ShooterTestWithPID.TestConfig.power2

@TeleOp
class ShooterTestWithPID : LinearOpMode() {
    @Configurable
    object TestConfig {
        @JvmField
        var power1 = 0.0
        @JvmField
        var power2 = 0.0

    }
    override fun runOpMode() {
        val panelsTelemetry = PanelsTelemetry.telemetry

        val timeKeep = TimeKeep()
        val motor = hardwareMap.get(DcMotorEx::class.java, "motorShooter1")
        val motor2 = hardwareMap.get(DcMotorEx::class.java, "motorShooter2")
        val voltageSensor = hardwareMap.voltageSensor.iterator().next()

        val robot = Robot(hardwareMap)
        waitForStart()

        while (opModeIsActive()) {
            timeKeep.resetDeltaTime()
            if (gamepad1.dpad_right) {
                power1= (power1 + 30 * timeKeep.deltaTime.asS).coerceIn(0.0,5000.0)
            }
            if (gamepad1.dpad_left) {
                power1= (power1 - 30 * timeKeep.deltaTime.asS).coerceIn(0.0,5000.0)
            }
            if (gamepad1.dpad_up) {
                power2= (power2 + 30 * timeKeep.deltaTime.asS).coerceIn(0.0,5000.0)
            }
            if (gamepad1.dpad_down) {
                power2= (power2 - 30 * timeKeep.deltaTime.asS).coerceIn(0.0,5000.0)
            }
            robot.shooter.goToRpm(power1, power2)
            robot.transfer.power = gamepad1.left_trigger.toDouble() - gamepad1.right_trigger.toDouble()

            robot.shooter.updateRpm(timeKeep.deltaTime)
            telemetry.addData("motor gecko rpm", robot.shooter.currentRpm_Gecko)
            telemetry.addData("motor gecko target rpm", robot.shooter.targetRpm_Gecko)
            telemetry.addData("motor rhino rpm", robot.shooter.currentRpm_Rhino)
            telemetry.addData("motor rhino target rpm", robot.shooter.targetRpm_Rhino)
            telemetry.update()
        }
    }
}