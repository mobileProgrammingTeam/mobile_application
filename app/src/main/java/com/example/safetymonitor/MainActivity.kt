package com.example.safetymonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.safetymonitor.coordinator.SafetyMonitorCoordinator
import com.example.safetymonitor.detection.beacon.BeaconDetector
import com.example.safetymonitor.detection.biometric.BiometricDetector
import com.example.safetymonitor.detection.fall.FallDetector
import com.example.safetymonitor.network.HttpAlertSender
import com.example.safetymonitor.ui.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 3개 감지기 객체 생성
        val fall = FallDetector()
        val bio = BiometricDetector()
        val beacon = BeaconDetector()

        // 2. 공통 판단 관리자 (로컬호스트 HTTP 전송기 연결)
        val coordinator = SafetyMonitorCoordinator(
            detectors = listOf(fall, bio, beacon),
            alertSender = HttpAlertSender("http://10.0.2.2:8080/alert")
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
