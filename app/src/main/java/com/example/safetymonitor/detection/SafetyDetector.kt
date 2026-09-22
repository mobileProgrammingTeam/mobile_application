package com.example.safetymonitor.detection

/**
 * 모든 감지 모듈이 구현해야 하는 공통 인터페이스 (팀 아키텍처 명세)
 */
interface SafetyDetector {
    val detectorType: DetectorType

    fun start()
    fun stop()

    fun setEventListener(
        listener: (SafetyEvent) -> Unit
    )
}
