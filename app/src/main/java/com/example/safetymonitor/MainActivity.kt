package com.example.safetymonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.safetymonitor.coordinator.SafetyMonitorCoordinator
import com.example.safetymonitor.detection.beacon.BeaconDetector
import com.example.safetymonitor.detection.biometric.BiometricDetector
import com.example.safetymonitor.detection.fall.FallDetector
import com.example.safetymonitor.network.MockAlertSender
import com.example.safetymonitor.ui.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 세 감지기 객체 생성
        val fall = FallDetector()
        val bio = BiometricDetector()
        val beacon = BeaconDetector()

        // 2. 관리자(Coordinator) 생성
        val coordinator = SafetyMonitorCoordinator(
            detectors = listOf(fall, bio, beacon),
            alertSender = MockAlertSender()
        )

        // 3. 화면 띄우기
        setContent {
            MainScreen(
                coordinator = coordinator,
                fallDetector = fall,
                bioDetector = bio,
                beaconDetector = beacon
            )
        }
    }
}
