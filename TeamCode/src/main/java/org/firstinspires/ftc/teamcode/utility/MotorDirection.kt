package org.firstinspires.ftc.teamcode.utility

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.Utility
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple

@Utility
class MotorDirection : LinearOpMode() {
    override fun runOpMode() {
        val motorLF = hardwareMap.get(DcMotorEx::class.java, "leftFront")
        val motorLB = hardwareMap.get(DcMotorEx::class.java, "leftBack")
        val motorRB = hardwareMap.get(DcMotorEx::class.java, "rightBack")
        val motorRF = hardwareMap.get(DcMotorEx::class.java, "rightFront")

        motorLF.direction = DcMotorSimple.Direction.FORWARD
        motorLB.direction = DcMotorSimple.Direction.FORWARD
        motorRB.direction = DcMotorSimple.Direction.REVERSE
        motorRF.direction = DcMotorSimple.Direction.REVERSE

        waitForStart()

        while (opModeIsActive()) {
            motorLF.power = if (gamepad1.x) 1.0 else 0.0
            motorRF.power = if (gamepad1.y) 1.0 else 0.0
            motorRB.power = if (gamepad1.b) 1.0 else 0.0
            motorLB.power = if (gamepad1.a) 1.0 else 0.0
        }
    }
}