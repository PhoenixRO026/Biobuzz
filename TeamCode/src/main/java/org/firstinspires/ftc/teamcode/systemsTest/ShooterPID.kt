package org.firstinspires.ftc.teamcode.systemsTest

import com.bylazar.configurables.annotations.Configurable
import com.bylazar.telemetry.PanelsTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.library.TimeKeep
import org.firstinspires.ftc.teamcode.library.controller.PIDController

@TeleOp
class ShooterPID : LinearOpMode() {
    @Configurable
    object ShooterTuningConfig {
        @JvmField
        var controller_Rhino = PIDController(
            kP = 0.0015,
            kD = 0.00001,
            kI = 0.00,
            stabilityThreshold = 50.0
        )
        @JvmField var kS_Rhino = 1.55
        @JvmField var kV_Rhino = 0.002267
        @JvmField var targetRpm_Rhino = 1000.0

        @JvmField
        var controller_Gecko = PIDController(
            kP = 0.0005,
            kD = 0.00001,
            kI = 0.00,
            stabilityThreshold = 50.0
        )
        @JvmField var kS_Gecko = 1.11
        @JvmField var kV_Gecko = 0.00242
        @JvmField var targetRpm_Gecko = 1000.0
    }

    override fun runOpMode() {
        val panelsTelemetry = PanelsTelemetry.telemetry

        val motorGecko = hardwareMap.get(DcMotorEx::class.java, "motorShooter1")
        val motorRhino = hardwareMap.get(DcMotorEx::class.java, "motorShooter2")
        motorGecko.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        motorRhino.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT


        val voltageSensor = hardwareMap.voltageSensor.iterator().next()

        val timeKeep = TimeKeep()

        waitForStart()

        while (opModeIsActive()) {
            timeKeep.resetDeltaTime()

            val currentRpm_Rhino = motorRhino.velocity * 60.0 / 28

            val pidPower_Rhino = ShooterTuningConfig.controller_Rhino.calculate(currentRpm_Rhino, ShooterTuningConfig.targetRpm_Rhino, timeKeep.deltaTime)
            val feedforwardPower_Rhino = ShooterTuningConfig.kS_Rhino + ShooterTuningConfig.kV_Rhino * ShooterTuningConfig.targetRpm_Rhino
            val voltage_Rhino = voltageSensor.voltage
            val shooterPower_Rhino = pidPower_Rhino + feedforwardPower_Rhino / voltage_Rhino

            motorRhino.power = shooterPower_Rhino

            panelsTelemetry.addData("currentRpm Rhino", currentRpm_Rhino)
            panelsTelemetry.addData("targetRpm Rhino", ShooterTuningConfig.targetRpm_Rhino)
            panelsTelemetry.addData("shooterPower Rhino", shooterPower_Rhino)

            val currentRpm_Gecko = motorGecko.velocity * 60.0 / 28

            val pidPower_Gecko = ShooterTuningConfig.controller_Gecko.calculate(currentRpm_Gecko, ShooterTuningConfig.targetRpm_Gecko, timeKeep.deltaTime)
            val feedforwardPower_Gecko = ShooterTuningConfig.kS_Gecko + ShooterTuningConfig.kV_Gecko * ShooterTuningConfig.targetRpm_Gecko
            val voltage_Gecko = voltageSensor.voltage
            val shooterPower_Gecko = pidPower_Gecko + feedforwardPower_Gecko / voltage_Gecko

            motorGecko.power = shooterPower_Gecko

            panelsTelemetry.addData("currentRpm Gecko", currentRpm_Gecko)
            panelsTelemetry.addData("targetRpm Gecko", ShooterTuningConfig.targetRpm_Gecko)
            panelsTelemetry.addData("shooterPower Gecko", shooterPower_Gecko)
            panelsTelemetry.update(telemetry)
        }
    }
}