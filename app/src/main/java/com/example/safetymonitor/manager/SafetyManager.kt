package com.example.safetymonitor.manager

import com.example.safetymonitor.detection.*
import com.example.safetymonitor.network.AlertSender

/**
 * 안전 관리자 (팀 아키텍처 명세: SafetyManager)
 * 각 감지기(Detector)의 이벤트를 받아 위험(ALERT) 상태 시 서버로 전송
 */
class SafetyManager(
    private val detectors: List<SafetyDetector>,
    private val alertSender: AlertSender
) {
    private val lastAlertTime = mutableMapOf<DetectorType, Long>()
    private val cooldownMillis = 5000L // 5초 중복 억제

    fun start(onUiUpdate: (SafetyEvent) -> Unit) {
        detectors.forEach { detector ->
            // 1. 각 감지기에 리스너 연결
            detector.setEventListener { event ->
                // UI 갱신
                onUiUpdate(event)

                // 위험 상태일 때 서버 전송
                if (event.status == DetectionStatus.ALERT) {
                    val now = System.currentTimeMillis()
                    val lastTime = lastAlertTime[event.type] ?: 0L

                    if (now - lastTime > cooldownMillis) {
                        lastAlertTime[event.type] = now
                        alertSender.send(event)
                    }
                }
            }

            // 2. 감지 시작
            detector.start()
        }
    }

    fun stop() {
        detectors.forEach { it.stop() }
    }
}
