package org.firstinspires.ftc.teamcode.auto

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.robot.Drive

@Config("Leave Auto")
@Autonomous(name = "Leave", preselectTeleOp = "Buzzy Drive")
class LeaveAuto : LinearOpMode() {

    companion object {
        @JvmField var sleepMillis = 1000L
    }

    override fun runOpMode() {
        val drive = Drive(hardwareMap)

        waitForStart()

        drive.driveRobotCentric(0.5, 0.0, 0.0)

        sleep(sleepMillis)

        drive.driveRobotCentric(0.0, 0.0 ,0.0)
    }
}