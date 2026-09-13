package org.firstinspires.ftc.teamcode.auto

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.robot.Drive

@Autonomous(name = "Leave", preselectTeleOp = "Buzzy Drive")
class LeaveAuto : LinearOpMode() {
    override fun runOpMode() {
        val drive = Drive(hardwareMap)

        waitForStart()

        drive.driveRobotCentric(0.5, 0.0, 0.0)

        sleep(1000)

        drive.driveRobotCentric(0.0, 0.0 ,0.0)
    }
}