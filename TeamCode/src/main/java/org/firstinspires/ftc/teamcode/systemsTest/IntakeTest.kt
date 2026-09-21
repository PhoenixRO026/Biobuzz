package org.firstinspires.ftc.teamcode.systemsTest

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorEx

@TeleOp
class IntakeTest : LinearOpMode() {
    override fun runOpMode() {
        val motor = hardwareMap.get(DcMotorEx::class.java, "intake")

        waitForStart()

        while (opModeIsActive()) {
            if (gamepad1.dpad_right) {
                motor.power = 1.0
            }
            if (gamepad1.dpad_left) {
                motor.power = -1.0
            }
            if (gamepad1.dpad_up) {
                motor.power = 0.0
            }
        }
    }
}