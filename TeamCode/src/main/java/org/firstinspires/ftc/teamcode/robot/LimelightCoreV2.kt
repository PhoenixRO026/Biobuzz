package org.firstinspires.ftc.teamcode.robot

import com.bylazar.configurables.annotations.Configurable
import com.bylazar.telemetry.TelemetryManager
import com.pedropathing.math.Pose
import com.qualcomm.hardware.limelightvision.LLResult
import com.qualcomm.hardware.limelightvision.Limelight3A
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.teamcode.library.controller.LowPassFilter
import org.psilynx.psikit.core.Logger
import kotlin.math.absoluteValue

class LimelightCoreV2(
    val limelight3A: Limelight3A
) {
    @Configurable
    data object LLCore2 {
        @JvmField
        var txFilter = LowPassFilter(0.3)
        @JvmField
        var headingToleranceDeg = 2.0
        @JvmField
        var botposeXFilter = LowPassFilter(0.3)
        @JvmField
        var botposeYFilter = LowPassFilter(0.3)
        @JvmField
        var botposeHFilter = LowPassFilter(0.3)
        @JvmField
        var aimCooldownS = 0.2
    }
    private var lastAimTime = Logger.getTimestamp()

    private var _latestResult: LLResult? = null
    val latestResult: LLResult? get() = _latestResult

    private var _freshResult = false
    val freshResult: Boolean get() = _freshResult

    private var _botPose = Pose(0.0, 0.0, 0.0)
    val botPose: Pose get() = _botPose

    private var _botPoseFiltered = Pose(0.0, 0.0, 0.0)
    val botPoseFiltered: Pose get() = _botPoseFiltered

    private var _freshBotPose = false
    val freshBotPose: Boolean get() = _freshBotPose

    private var _txDeg: Double = 0.0
    val txDeg: Double get() = _txDeg

    private var _freshTx = false
    val freshTx: Boolean get() = _freshTx

    private var _txDegFiltered: Double = 0.0
    val txDegFiltered: Double get() = _txDegFiltered

    private fun updateBotpose(result: LLResult) {
        val pose3d = result.botpose ?: return

        // Convert FTC center-origin (-72 to +72 in) to Pedro 3.x bottom-left origin (0 to 144 in)
        val xInches = pose3d.position.unit.toInches(pose3d.position.x) + 72.0
        val yInches = pose3d.position.unit.toInches(pose3d.position.y) + 72.0
        val heading = pose3d.orientation.getYaw(AngleUnit.RADIANS)

        val pedroPose = Pose(xInches, yInches, heading)
        _botPose = pedroPose
        filterBotpose(pedroPose)
        _freshBotPose = true
    }

    private fun filterBotpose(pose: Pose) {
        _botPoseFiltered = Pose(
            LLCore2.botposeXFilter.estimate(pose.x()),
            LLCore2.botposeYFilter.estimate(pose.y()),
            LLCore2.botposeHFilter.estimate(pose.heading())
        )
    }

    private fun updateTx(result: LLResult) {
        val tx = result.tx
        _txDeg = tx
        _txDegFiltered = LLCore2.txFilter.estimate(tx)
        _freshTx = true
    }

    fun update() {
        _freshResult = false
        _freshBotPose = false
        _freshTx = false
        val result = limelight3A.latestResult ?: return
        if (!result.isValid) return
        _latestResult = result
        _freshResult = true

        updateBotpose(result)
        updateTx(result)
    }

    fun aimOnCooldown() = Logger.getTimestamp() - lastAimTime < LLCore2.aimCooldownS

    fun resetAimCooldown() {
        lastAimTime = Logger.getTimestamp()
    }

    fun addTelemetry(telemetryManager: TelemetryManager) {
        with(telemetryManager) {
            addLine("limelight telemetry")
            addData("is connected", limelight3A.isConnected)
            addData("is running", limelight3A.isRunning)
            addData("is result valid", latestResult?.isValid ?: false)
            addData("tx deg", txDeg)
            addData("tx filtered", txDegFiltered)
            botPose.apply {
                addData("botpose x", x())
                addData("botpose y", y())
                addData("botpose heading", heading())
            }
            botPoseFiltered.apply {
                addData("botpose filtered x", x())
                addData("botpose filtered y", y())
                addData("botpose filtered heading", heading())
            }
        }
    }

    fun switchPipeline(index: Int) {
        limelight3A.pipelineSwitch(index)
    }

    fun start() {
        limelight3A.start()
    }

    fun withinTolerance(firstAngle: Double, secondAngle: Double): Boolean {
        return (firstAngle - secondAngle).absoluteValue < LLCore2.headingToleranceDeg
    }
}
