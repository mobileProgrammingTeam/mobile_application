package com.example.safetymonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.safetymonitor.detection.beacon.BeaconDetector
import com.example.safetymonitor.detection.fall.FallDetector
import com.example.safetymonitor.detection.vital.VitalDetector
import com.example.safetymonitor.manager.SafetyManager
import com.example.safetymonitor.network.HttpAlertSender
import com.example.safetymonitor.ui.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 세 감지기 객체 생성 (팀 아키텍처 명세)
        val fall = FallDetector()
        val vital = VitalDetector()
        val beacon = BeaconDetector()

        // 2. 공통 관리자 (SafetyManager)
        val safetyManager = SafetyManager(
            detectors = listOf(fall, vital, beacon),
            alertSender = HttpAlertSender("http://10.0.2.2:8080/alert")
        )

        // 3. 화면 띄우기
        setContent {
            MainScreen(
                safetyManager = safetyManager,
                fallDetector = fall,
                vitalDetector = vital,
                beaconDetector = beacon
            )
        }
    }
}
