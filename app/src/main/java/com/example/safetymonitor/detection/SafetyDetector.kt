package com.example.safetymonitor.detection

/**
 * 감지기 공통 인터페이스.
 * Java의 public interface SafetyDetector { ... } 와 완전히 같습니다.
 */
interface SafetyDetector {
    val type: DetectorType

    // 감지 시작 (결과가 나오면 onResult 콜백 함수를 실행)
    fun start(onResult: (DetectionResult) -> Unit)

    // 감지 중지
    fun stop()
}
