package com.example.safetymonitor.coordinator

import com.example.safetymonitor.detection.*
import com.example.safetymonitor.network.AlertSender

/**
 * 공통 판단 관리자 (팀원들의 감지 결과를 받아 처리)
 */
class SafetyMonitorCoordinator(
    private val detectors: List<SafetyDetector>,
    private val alertSender: AlertSender
) {
    // 기능별 마지막 알림 시간 (연속 중복 알림 방지용)
    private val lastAlertTime = mutableMapOf<DetectorType, Long>()
    private val cooldownMillis = 5000L // 5초 동안 중복 알림 무시

    fun startAll(onUiUpdate: (DetectionResult) -> Unit) {
        detectors.forEach { detector ->
            detector.start { result ->
                // 1. 화면에 상태 갱신
                onUiUpdate(result)

                // 2. 위험 상태(ALERT)일 때만 서버 전송 검토
                if (result.status == DetectionStatus.ALERT) {
                    val now = System.currentTimeMillis()
                    val lastTime = lastAlertTime[result.type] ?: 0L

                    // 5초 쿨다운 지난 경우에만 전송
                    if (now - lastTime > cooldownMillis) {
                        lastAlertTime[result.type] = now
                        alertSender.send(result)
                    }
                }
            }
        }
    }

    fun stopAll() {
        detectors.forEach { it.stop() }
    }
}
