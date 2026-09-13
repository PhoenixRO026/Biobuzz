package org.firstinspires.ftc.teamcode.utility

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.Utility
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.robot.HardwareNames

@Utility
class MotorDirection : LinearOpMode() {
    override fun runOpMode() {
        val leftFront: DcMotor =  hardwareMap.get(DcMotor::class.java, HardwareNames.motorLeftFront)
        val leftBack: DcMotor =hardwareMap.get(DcMotor::class.java, HardwareNames.motorLeftBack)
        val rightFront: DcMotor =hardwareMap.get(DcMotor::class.java, HardwareNames.motorRightFront)
        val rightBack: DcMotor =hardwareMap.get(DcMotor::class.java, HardwareNames.motorRightBack)

        leftFront.direction = DcMotorSimple.Direction.FORWARD
        leftBack.direction = DcMotorSimple.Direction.FORWARD
        rightFront.direction = DcMotorSimple.Direction.REVERSE
        rightBack.direction = DcMotorSimple.Direction.REVERSE

        waitForStart()

        while (opModeIsActive()) {
            leftFront.power = if (gamepad1.x) 1.0 else 0.0
            rightFront.power = if (gamepad1.y) 1.0 else 0.0
            rightBack.power = if (gamepad1.b) 1.0 else 0.0
            leftBack.power = if (gamepad1.a) 1.0 else 0.0
        }
    }
}