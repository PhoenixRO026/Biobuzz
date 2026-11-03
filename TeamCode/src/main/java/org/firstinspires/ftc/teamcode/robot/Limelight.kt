package org.firstinspires.ftc.teamcode.robot

import com.qualcomm.hardware.limelightvision.Limelight3A
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D
import com.pedropathing.math.Pose
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import kotlin.math.PI

class Limelight(
    val camera : Limelight3A
) {
    var headingErrorDeg: Double = 0.0
        private set

    var tagVisible: Boolean = false
        private set

    var aprilTagDistance: Double = 0.0
        private set

    var limelightPose: Pose = Pose(0.0, 0.0, 0.0)
        private set

    var limelightPoseValid: Boolean = false

    data class LLResult(
        val pose: Pose,
        val timestamp: Long
    )

    fun getNewPose(): LLResult? {
        val result = camera.latestResult ?: return null

        if (!(result.isValid && result.staleness > 200)) {
            return null
        }

        val botpose = result.botpose

        val variance = result.stddevMt1

        val posInInches = botpose.position.toUnit(DistanceUnit.INCH)

        val ftcX = posInInches.x
        val ftcY = posInInches.y
        val ftcHeading = botpose.orientation.getYaw(AngleUnit.RADIANS)

        val pose = Pose(
            ftcY + 72.0,
            72.0 - ftcX,
            ftcHeading - (PI / 2.0)
        )

        val timestamp = result.controlHubTimeStamp

        return LLResult(pose, timestamp)
    }

    fun updateLimelightPose() {
        val result = camera.latestResult ?: run {
            aprilTagDistance = Double.NaN
            limelightPoseValid = false
            return
        }

        if (!result.isValid || result.staleness > 100) {
            aprilTagDistance = Double.NaN
            limelightPoseValid = false
            return
        }

        val fid = result.fiducialResults
            .firstOrNull { it.fiducialId in listOf(20, 24) }
            ?: run {
                aprilTagDistance = Double.NaN
                limelightPoseValid = false
                return
            }

        val pose = fid.robotPoseFieldSpace ?: run {
            limelightPoseValid = false
            return
        }

        val ftcPose = Pose2D(
            pose.position.unit,
            pose.position.x,
            pose.position.y,
            AngleUnit.RADIANS,
            pose.orientation.getYaw(AngleUnit.RADIANS)
        )

        val xInches = pose.position.unit.toInches(pose.position.x)
        val yInches = pose.position.unit.toInches(pose.position.y)
        val yawRad = pose.orientation.getYaw(AngleUnit.RADIANS)


        limelightPose = Pose(
            xInches + 72.0,
            yInches + 72.0,
            yawRad
        )
        limelightPoseValid = true
    }

    fun updateDistance() {
        val fid = camera.latestResult
            ?.fiducialResults
            ?.firstOrNull { it.fiducialId in listOf(20, 24) }
            ?: run { aprilTagDistance = Double.NaN; return }

        val pose = fid.targetPoseCameraSpace ?: run {
            aprilTagDistance = 0.0
            return
        }

        val x = pose.position.x
        val y = pose.position.y
        val z = pose.position.z

        aprilTagDistance = kotlin.math.sqrt(x * x + y * y + z * z) * 100
    }

    fun setPipeline(index: Int) {
        camera.pipelineSwitch(index)
    }

    fun updateHeadingError() {
        val fid = camera.latestResult
            ?.fiducialResults
            ?.firstOrNull()

        if (fid != null) {
            headingErrorDeg = -fid.targetXDegrees
            tagVisible = true
        } else {
            headingErrorDeg = 0.0
            tagVisible = false
        }
    }
}