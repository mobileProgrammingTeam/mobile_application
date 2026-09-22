package com.example.safetymonitor.detection.fall

import com.example.safetymonitor.detection.*

class FallDetector : SafetyDetector {
    override val detectorType = DetectorType.FALL
    private var listener: ((SafetyEvent) -> Unit)? = null

    override fun setEventListener(listener: (SafetyEvent) -> Unit) {
        this.listener = listener
    }

    override fun start() {
        // TODO: 실제 가속도 센서 리스너 등록
    }

    override fun stop() {
        // TODO: 실제 가속도 센서 리스너 해제
    }

    // 테스트용 이벤트 발생 함수
    fun testTriggerAlert() {
        listener?.invoke(
            SafetyEvent(
                type = DetectorType.FALL,
                status = DetectionStatus.ALERT,
                message = "낙상 감지됨 (충격 발생)"
            )
        )
    }
}
