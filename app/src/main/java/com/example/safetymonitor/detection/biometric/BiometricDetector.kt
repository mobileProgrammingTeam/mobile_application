package com.example.safetymonitor.detection.biometric

import com.example.safetymonitor.detection.*

class BiometricDetector : SafetyDetector {
    override val type = DetectorType.BIOMETRIC
    private var callback: ((DetectionResult) -> Unit)? = null

    override fun start(onResult: (DetectionResult) -> Unit) {
        this.callback = onResult
    }

    override fun stop() {
        this.callback = null
    }

    // 테스트용: 비정상 심박수 감지 이벤트 발생
    fun testTriggerAlert() {
        callback?.invoke(
            DetectionResult(
                type = DetectorType.BIOMETRIC,
                status = DetectionStatus.ALERT,
                message = "비정상 심박수 감지 (145 bpm)"
            )
        )
    }
}
