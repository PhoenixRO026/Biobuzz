package org.firstinspires.ftc.teamcode.robot

import com.pedropathing.follower.Follower
import com.qualcomm.hardware.limelightvision.Limelight3A
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.IMU
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.teamcode.pedro.Constants

class Robot(
    hardwareMap: HardwareMap
) {
//    val follower : Follower = Constants.create(hardwareMap)
    val drive : ImuDrive
    val intake : Intake
    val shooter: Shooter
    val transfer: Transfer
    val limelight : Limelight

    init {
        // Drive
        val motorLF = hardwareMap.get(DcMotorEx::class.java, "leftFront")
        val motorLB = hardwareMap.get(DcMotorEx::class.java, "leftBack")
        val motorRB = hardwareMap.get(DcMotorEx::class.java, "rightBack")
        val motorRF = hardwareMap.get(DcMotorEx::class.java, "rightFront")
        listOf(motorLF, motorLB, motorRB, motorRF).forEach {
            it.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
            it.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        }
        motorLF.direction = DcMotorSimple.Direction.FORWARD
        motorLB.direction = DcMotorSimple.Direction.FORWARD
        motorRB.direction = DcMotorSimple.Direction.REVERSE
        motorRF.direction = DcMotorSimple.Direction.REVERSE

        val imu = hardwareMap.get(IMU::class.java, "imu")
        imu.initialize(
            IMU.Parameters(
                RevHubOrientationOnRobot(ImuDrive.ImuDriveConfig.logoDirection, ImuDrive.ImuDriveConfig.usbDirection)
            )
        )
        imu.resetYaw()

        // Shooter
        val motorShooterLeft = hardwareMap.get(DcMotorEx::class.java, "shooterLeft")
        val motorShooterRight = hardwareMap.get(DcMotorEx::class.java, "shooterRight")
        motorShooterLeft.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        motorShooterLeft.direction = DcMotorSimple.Direction.FORWARD
        motorShooterLeft.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        motorShooterRight.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        motorShooterRight.direction = DcMotorSimple.Direction.REVERSE
        motorShooterRight.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        val voltageSensor = hardwareMap.get(VoltageSensor::class.java, "Control Hub")

        // Limelight
        val camera = hardwareMap.get(Limelight3A::class.java, "limelight")
        camera.setPollRateHz(60)
        camera.start()

        // Intake
        val motorIntake = hardwareMap.get(DcMotorEx::class.java, "intake")
        motorIntake.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        motorIntake.direction = DcMotorSimple.Direction.REVERSE
        motorIntake.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        // Transfer
        val motorTransfer = hardwareMap.get(DcMotorEx::class.java, "transfer")
        motorTransfer.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        motorTransfer.direction = DcMotorSimple.Direction.REVERSE
        motorTransfer.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

//        drive = Drive(follower)
        drive = ImuDrive(
            motorLF = motorLF,
            motorLB = motorLB,
            motorRB = motorRB,
            motorRF = motorRF,
            imu = imu
        )
        shooter = Shooter(
            motorLeft = motorShooterLeft,
            motorRight = motorShooterRight,
            voltageSensor = voltageSensor
        )
        transfer = Transfer(
            motor = motorTransfer
        )
        intake = Intake(
            motor = motorIntake
        )
        limelight = Limelight(
            camera = camera
        )

        shooter.targetRpm = 0.0
        shooter.shooterPower = 0.0
        intake.stopIntake()
        transfer.stopTransfer()
    }
}