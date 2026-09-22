package com.example.safetymonitor.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.safetymonitor.coordinator.SafetyMonitorCoordinator
import com.example.safetymonitor.detection.beacon.BeaconDetector
import com.example.safetymonitor.detection.biometric.BiometricDetector
import com.example.safetymonitor.detection.fall.FallDetector

@Composable
fun MainScreen(
    coordinator: SafetyMonitorCoordinator,
    fallDetector: FallDetector,
    bioDetector: BiometricDetector,
    beaconDetector: BeaconDetector
) {
    // 화면에 보여줄 마지막 상태 텍스트
    var statusText by remember { mutableStateOf("모니터링 대기 중") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("👷 작업자 안전 모니터링 MVP", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        // 모니터링 시작/중지 버튼
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                coordinator.startAll { result ->
                    statusText = "[${result.type}] ${result.status} : ${result.message}"
                }
                statusText = "모니터링 시작됨"
            }) {
                Text("시작")
            }

            Button(onClick = {
                coordinator.stopAll()
                statusText = "모니터링 중지됨"
            }) {
                Text("중지")
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("현재 상태: $statusText", style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(24.dp))
        Text("테스트용 버튼 (임의 위험 발생):")
        Spacer(Modifier.height(8.dp))

        Button(onClick = { fallDetector.testTriggerAlert() }) {
            Text("1. 낙상 위험 발생")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { bioDetector.testTriggerAlert() }) {
            Text("2. 심박수 위험 발생")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { beaconDetector.testTriggerAlert() }) {
            Text("3. 위험구역 진입 발생")
        }
    }
}
