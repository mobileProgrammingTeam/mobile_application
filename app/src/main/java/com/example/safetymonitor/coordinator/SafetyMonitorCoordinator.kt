package com.example.safetymonitor.coordinator

import com.example.safetymonitor.detection.*
import com.example.safetymonitor.network.AlertSender

/**
 * 공통 판단 관리자
 * 각 감지기의 요청을 받아 위험(ALERT) 상태이면 중복 확인 후 서버로 전송
 */
class SafetyMonitorCoordinator(
    private val detectors: List<SafetyDetector>,
    private val alertSender: AlertSender
) {
    // 5초 동안 동일 타입의 중복 알림 전송 방지
    private val lastAlertTime = mutableMapOf<DetectorType, Long>()
    private val cooldownMillis = 5000L

    fun startAll(onUiUpdate: (DetectionResult) -> Unit) {
        detectors.forEach { detector ->
            detector.start { result ->
                // 1. 화면에 상태 갱신
                onUiUpdate(result)

                // 2. 위험 상태(ALERT)일 때 서버 전송
                if (result.status == DetectionStatus.ALERT) {
                    val now = System.currentTimeMillis()
                    val lastTime = lastAlertTime[result.type] ?: 0L

                    if (now - lastTime > cooldownMillis) {
                        lastAlertTime[result.type] = now
                        alertSender.send(result) // HTTP JSON 전송 요청
                    }
                }
            }
        }
    }

    fun stopAll() {
        detectors.forEach { it.stop() }
    }
}
