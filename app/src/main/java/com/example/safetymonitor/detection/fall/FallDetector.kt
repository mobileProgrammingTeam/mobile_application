package com.example.safetymonitor.detection.fall

import com.example.safetymonitor.detection.*

class FallDetector : SafetyDetector {
    override val type = DetectorType.FALL
    private var callback: ((DetectionResult) -> Unit)? = null

    override fun start(onResult: (DetectionResult) -> Unit) {
        this.callback = onResult
    }

    override fun stop() {
        this.callback = null
    }

    // 테스트용: 낙상 감지 이벤트 발생
    fun testTriggerAlert() {
        callback?.invoke(
            DetectionResult(
                type = DetectorType.FALL,
                status = DetectionStatus.ALERT,
                message = "낙상 감지됨 (급격한 충격)"
            )
        )
    }
}
