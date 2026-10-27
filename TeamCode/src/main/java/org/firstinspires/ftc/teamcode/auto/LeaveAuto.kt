package org.firstinspires.ftc.teamcode.auto

import com.bylazar.configurables.annotations.Configurable
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
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

        // the follower only sends the powers to the motors in update(), so it has to be called in a loop
        val timer = ElapsedTime()
        robot.drive.driveRobotCentric(0.5, 0.0, 0.0)

        while (opModeIsActive() && timer.milliseconds() < LeaveAutoConfig.sleepMillis) {
            robot.follower.update()
        }

        robot.drive.driveRobotCentric(0.0, 0.0, 0.0)
        robot.follower.update()
    }
}