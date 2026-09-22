package com.example.safetymonitor.detection.beacon

import com.example.safetymonitor.detection.*

class BeaconDetector : SafetyDetector {
    override val type = DetectorType.BEACON
    private var callback: ((DetectionResult) -> Unit)? = null

    override fun start(onResult: (DetectionResult) -> Unit) {
        this.callback = onResult
    }

    override fun stop() {
        this.callback = null
    }

    // 테스트용: 위험구역 진입 이벤트 발생
    fun testTriggerAlert() {
        callback?.invoke(
            DetectionResult(
                type = DetectorType.BEACON,
                status = DetectionStatus.ALERT,
                message = "위험구역 진입 감지 (비콘 ID: DANGER_01)"
            )
        )
    }
}
