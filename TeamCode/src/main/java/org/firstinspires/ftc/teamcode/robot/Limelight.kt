package org.firstinspires.ftc.teamcode.robot

import com.bylazar.configurables.annotations.Configurable
import com.qualcomm.hardware.limelightvision.LLResult
import com.qualcomm.hardware.limelightvision.Limelight3A
import org.firstinspires.ftc.teamcode.library.controller.LowPassFilter

class Limelight(
    val camera : Limelight3A
) {
    @Configurable
    object LimelightConfig {
        // pipeline indexes, as set up in the limelight web interface (http://limelight.local:5801)
        @JvmField var qrPipeline = 0
        @JvmField var pollenPipeline = 1

        // class name of the pollen in a trained neural detector model (ignored for color pipelines)
        @JvmField var pollenClassName = "pollen"

        @JvmField var maxStalenessMs = 100L
        @JvmField var txFilter = LowPassFilter(0.3)
    }

    var latestResult : LLResult? = null
        private set

    var freshResult = false
        private set

    // QR codes seen in the latest frame
    var qrCodes : List<String> = emptyList()
        private set

    // last QR code ever seen, stays after the code leaves the frame
    var lastQrCode : String? = null
        private set

    var pollenVisible = false
        private set

    // horizontal angle to the pollen, positive = pollen is to the right
    var pollenTx = 0.0
        private set

    var pollenTxFiltered = 0.0
        private set

    // vertical angle to the pollen, positive = pollen is up
    var pollenTy = 0.0
        private set

    // how much of the image the pollen takes (0 - 100 %), bigger = closer
    var pollenArea = 0.0
        private set

    val isConnected get() = camera.isConnected

    val pipelineIndex get() = latestResult?.pipelineIndex ?: -1

    fun update() {
        freshResult = false
        qrCodes = emptyList()
        pollenVisible = false

        val result = camera.latestResult ?: return
        latestResult = result
        if (result.staleness > LimelightConfig.maxStalenessMs) return
        freshResult = true

        updateQr(result)
        updatePollen(result)
    }

    private fun updateQr(result: LLResult) {
        qrCodes = result.barcodeResults.mapNotNull { it.data }
        if (qrCodes.isNotEmpty()) {
            lastQrCode = qrCodes.first()
        }
    }

    // works with a neural detector pipeline (trained model) or a color pipeline, takes the biggest target
    private fun updatePollen(result: LLResult) {
        val detected = result.detectorResults
            .filter { it.className == LimelightConfig.pollenClassName }
            .maxByOrNull { it.targetArea }
        val colored = result.colorResults
            .maxByOrNull { it.targetArea }

        if (detected != null) {
            setPollen(detected.targetXDegrees, detected.targetYDegrees, detected.targetArea)
        }
        else if (colored != null) {
            setPollen(colored.targetXDegrees, colored.targetYDegrees, colored.targetArea)
        }
    }

    private fun setPollen(tx: Double, ty: Double, area: Double) {
        pollenVisible = true
        pollenTx = tx
        pollenTxFiltered = LimelightConfig.txFilter.estimate(tx)
        pollenTy = ty
        pollenArea = area
    }

    fun switchPipeline(index: Int) {
        camera.pipelineSwitch(index)
    }

    fun qrPipeline() { switchPipeline(LimelightConfig.qrPipeline) }
    fun pollenPipeline() { switchPipeline(LimelightConfig.pollenPipeline) }
}