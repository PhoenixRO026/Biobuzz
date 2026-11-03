package org.firstinspires.ftc.teamcode.systemsTest

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.library.TimeKeep

@TeleOp
class ShooterTest : LinearOpMode() {
    override fun runOpMode() {
        val timeKeep = TimeKeep()
        val motor = hardwareMap.get(DcMotorEx::class.java, "motorShooter1")
        val motor2 = hardwareMap.get(DcMotorEx::class.java, "motorShooter2")
        var power1 = 0.0
        var power2 = 0.0
        waitForStart()

        while (opModeIsActive()) {
            timeKeep.resetDeltaTime()
            if (gamepad1.dpad_right) {
                power1= (power1 + 0.05 * timeKeep.deltaTime.asS).coerceIn(-1.0,1.0)
            }
            if (gamepad1.dpad_left) {
                power1= (power1 - 0.05 * timeKeep.deltaTime.asS).coerceIn(-1.0,1.0)
            }
            if (gamepad1.dpad_up) {
                power2= (power2 + 0.05 * timeKeep.deltaTime.asS).coerceIn(-1.0,1.0)
            }
            if (gamepad1.dpad_down) {
                power2= (power2 - 0.05 * timeKeep.deltaTime.asS).coerceIn(-1.0,1.0)
            }
            motor.power = power1
            motor2.power = power2


            telemetry.addData("motor 1 vel", motor.velocity)
            telemetry.addData("motor 2 vel", power2)
            telemetry.addData("motor 1 power", power1)
            telemetry.addData("motor 2 power", power2)
            telemetry.update()
        }
    }
}