package org.firstinspires.ftc.teamcode.systemsTest

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorEx

@TeleOp
class IntakeTest : LinearOpMode() {
    override fun runOpMode() {
        val motor = hardwareMap.get(DcMotorEx::class.java, "intake")
        val motor2 = hardwareMap.get(DcMotorEx::class.java, "transfer")
        val motor3 = hardwareMap.get(DcMotorEx::class.java, "motorShooter1")
        val motor4 = hardwareMap.get(DcMotorEx::class.java, "motorShooter2")

        waitForStart()

        while (opModeIsActive()) {
            if (gamepad1.dpad_right) {
                motor2.power = 1.0
            }
            if (gamepad1.dpad_left) {
                motor2.power = -1.0
            }
            else {
                motor2.power = 0.0
            }
            if (gamepad1.dpad_up) {
                motor3.power = 0.6
            }
            if (gamepad1.dpad_down) {
                motor3.power = -0.6
            }
            else {
                motor3.power = 0.0
            }
            motor4.power = gamepad1.left_trigger.toDouble() - gamepad1.right_trigger.toDouble()
        }
    }
}