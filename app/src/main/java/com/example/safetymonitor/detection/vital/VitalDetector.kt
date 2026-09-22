package com.example.safetymonitor.detection.vital

import com.example.safetymonitor.detection.*

/**
 * 스마트워치 생체신호 감지 모듈 (심박수, SpO2 등)
 */
class VitalDetector : SafetyDetector {
    override val detectorType = DetectorType.VITAL
    private var listener: ((SafetyEvent) -> Unit)? = null

    override fun setEventListener(listener: (SafetyEvent) -> Unit) {
        this.listener = listener
    }

    override fun start() {
        // TODO: 스마트워치 데이터 수신 시작
    }

    override fun stop() {
        // TODO: 스마트워치 데이터 수신 중지
    }

    // 테스트용 이벤트 발생 함수
    fun testTriggerAlert() {
        listener?.invoke(
            SafetyEvent(
                type = DetectorType.VITAL,
                status = DetectionStatus.ALERT,
                message = "비정상 심박수 감지 (145 bpm)"
            )
        )
    }
}
