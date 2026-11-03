package org.firstinspires.ftc.teamcode.robot

import com.bylazar.field.Style
import com.commonlibs.units.degToRad
import com.commonlibs.units.mmToInch
import com.pedropathing.follower.Follower
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.commands.Commands.instant
import com.pedropathing.ivy.commands.Commands.waitMs
import com.pedropathing.ivy.commands.Commands.waitUntil
import com.pedropathing.ivy.groups.Groups.parallel
import com.pedropathing.ivy.groups.Groups.race
import com.pedropathing.ivy.groups.Groups.sequential
import com.pedropathing.localization.FusionLocalizer
import com.pedropathing.math.Pose
import com.qualcomm.hardware.limelightvision.Limelight3A
import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.auto.AutoMemory
import org.firstinspires.ftc.teamcode.library.limelightToRobotPos
import org.firstinspires.ftc.teamcode.pedro.Constants
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt

class Robot(
    hardwareMap: HardwareMap,
    pose: Pose = Pose(0.0, 0.0, Math.toRadians(0.0))
) {
    val follower : Follower = Constants.create(hardwareMap)
    val drive : Drive
    val intake : Intake
    val shooter: Shooter
    val transfer: Transfer
    val limelight : Limelight
    val llCoreV2 : LimelightCoreV2


    val cameraLook: Style = Style(
        "", "#3fb560", 0.75
    )
    val convertLook: Style = Style(
        "", "#070ff5", 0.75
    )
    /*val fusionLocalizer = follower.poseTracker.localizer as? FusionLocalizer

    fun updateFusion() {
        if (fusionLocalizer == null) return

        val llResult = limelight.getNewPose() ?: return

        fusionLocalizer.addMeasurement(llResult.pose, llResult.timestamp)
    }*/

    fun rememberPose() {
        AutoMemory.lastAutoPose = follower.pose()
    }

    // return true if it has a target and doesnt need odometry aiming
    fun updateLLAim(): Boolean {
        llCoreV2.update()
        if (llCoreV2.aimOnCooldown()) return true
        if (!llCoreV2.freshTx) return false
        if (llCoreV2.withinTolerance(shooter.turretAngle, llCoreV2.txDegFiltered)) return true
        shooter.turretAngle += llCoreV2.txDegFiltered
        llCoreV2.resetAimCooldown()
        return true
    }
    init {
        follower.setPose(pose)
        follower.update()

        // Limelight
        val camera = hardwareMap.get(Limelight3A::class.java, "limelight")
        camera.setPollRateHz(60)
        camera.start()

        // Shooter
        val motorShooterLeft = hardwareMap.get(DcMotorEx::class.java, "shooterLeft")
        val motorShooterRight = hardwareMap.get(DcMotorEx::class.java, "shooterRight")
        motorShooterLeft.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        motorShooterLeft.direction = DcMotorSimple.Direction.FORWARD
        motorShooterLeft.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        motorShooterRight.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        motorShooterRight.direction = DcMotorSimple.Direction.REVERSE
        motorShooterRight.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        val servoTurret1 = hardwareMap.get(Servo::class.java, "turretServo1")
        val servoTurret2 = hardwareMap.get(Servo::class.java, "turretServo2")

        val motorRB = hardwareMap.get(DcMotorEx::class.java, "motorRB")
        val voltageSensor = hardwareMap.voltageSensor.iterator().next()


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

        val finger = hardwareMap.get(Servo::class.java, "finger")
        finger.scaleRange(0.4, 0.609)
        val distanceSensor = hardwareMap.get(AnalogInput::class.java, "distanceSensor")

        drive = Drive(follower)

        shooter = Shooter(
            motorRhino = motorShooterLeft,
            motorGecko = motorShooterRight,
            servo1 = servoTurret1,
            servo2 = servoTurret2,
            finger = finger,
            voltageSensor = voltageSensor,
            motorEncoder = motorRB,
        )
        transfer = Transfer(
            motor = motorTransfer,
            distanceSensor = distanceSensor
        )
        intake = Intake(
            motor = motorIntake
        )
        limelight = Limelight(
            camera = camera
        )
        llCoreV2 = LimelightCoreV2(
            limelight3A = camera
        )

        shooter.targetRpm_Rhino = 0.0
        shooter.motorPower_Rhino = 0.0
        intake.stopIntake()
        transfer.stopTransfer()
    }
    enum class Side { RED, BLUE}

    var BlueGoal = Pose(10.0, 135.0)
    var RedGoal = Pose(134.0, 135.0)

    fun distanceFromGoal(side : Side) : Double {
        val goal : Pose

        if (side == Side.RED)
            goal = RedGoal
        else
            goal = BlueGoal

        val d = sqrt((follower.pose().x() - goal.x()).pow(2) + (follower.pose().y() - goal.y()).pow(2)) * 2.54 - 12
        drive.distanceFromGoal = d
        return d
    }

    fun neededTurretAngle(side : Side) : Double {
        val goal : Pose

        if (side == Side.RED)
            goal = RedGoal
        else
            goal = BlueGoal


        val robotHeading = Math.toDegrees(follower.pose().heading())


        val normalAngle = 180.0 * robotHeading.sign * (-1) + robotHeading
        val offset = 67.026.mmToInch()

        val newRobotPose = Pose(follower.pose().x() + offset * cos(normalAngle.degToRad()), follower.pose().y() + offset * sin(normalAngle.degToRad()), robotHeading)

        val dx = goal.x() - newRobotPose.x()
        val dy = goal.y() - newRobotPose.y()

        val robotAngle = Math.toDegrees(atan2(dy, dx))

        var turretAngle = robotAngle - robotHeading

        // Normalize to [-180, 180]
        while (turretAngle > 180.0)
            turretAngle -= 360.0

        while (turretAngle < -180.0)
            turretAngle += 360.0

        return -turretAngle
    }

    fun updateHeading(side: Side) {
        val angle = neededTurretAngle(side)

        if (!angle.isFinite()) {
            return
        }

        shooter.turretGoToAngle(angle.coerceIn(-90.0, 90.0))
    }

    fun updateHeadingLL(side: Side) {
        val llAimGood = updateLLAim()
        if (!llAimGood) {
            updateHeading(side)
        }
    }

    fun resetRobotPose() {
        limelight.updateLimelightPose()

        if (!limelight.limelightPoseValid) {
            return
        }

        val cameraPose = limelight.limelightPose
        //DrawingClone.drawRobot(cameraPose, cameraLook)
        val turretHeading = Math.toRadians(shooter.turretAngle)

        val newRobotPosition = limelightToRobotPos(
            cameraPose,
            turretHeading
        )

        //DrawingClone.drawRobot(newRobotPosition, convertLook)

//        DrawingClone.sendPacket()

        //debug
        follower.setPose(newRobotPosition)
    }

    fun resetRobotPoseCommand() : Command = sequential(
        instant { resetRobotPose() },
    )

    fun allStartCommand() : Command = parallel(
        intake.startIntakeCommand(),
        transfer.startTransferCommand()
    )

    fun allStopCommand() : Command = parallel(
        intake.stopIntakeCommand(),
        transfer.stopTransferCommand()
    )

    fun intakeBalls(time: Double = 2500.0): Command =
        sequential (
            shooter.closeFingerCommand(),
            waitMs(100.0),
            intake.startIntakeCommand(),
            transfer.startTransferCommand(),
            race (
                waitUntil { transfer.isBallPresent() },
                waitMs(2000.0)
            ),
            transfer.slowTransferCommand(),
        )



    fun ejectBalls() : Command = sequential(
        intake.reverseIntakeCommand(),
        shooter.openFingerCommand(),
        transfer.reverseTransferCommand(),
        waitMs(500.0),
        transfer.stopTransferCommand(),
        intake.stopIntakeCommand()
    )

}