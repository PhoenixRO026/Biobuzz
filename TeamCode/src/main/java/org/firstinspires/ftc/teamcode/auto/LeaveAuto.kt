package org.firstinspires.ftc.teamcode.auto

import com.bylazar.configurables.annotations.Configurable
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.robot.Robot

@Autonomous(name = "Leave", preselectTeleOp = "Buzzy Drive")
class LeaveAuto : LinearOpMode() {
    @Configurable
    object LeaveAutoConfig {
        @JvmField var sleepMillis = 1000L
    }

    override fun runOpMode() {
        val robot = Robot(hardwareMap)

        waitForStart()

        robot.drive.driveRobotCentric(0.5, 0.0, 0.0)

        sleep(LeaveAutoConfig.sleepMillis)

        robot.drive.driveRobotCentric(0.0, 0.0, 0.0)
    }
}