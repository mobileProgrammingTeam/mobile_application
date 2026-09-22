package com.example.safetymonitor.detection

// 감지 종류
enum class DetectorType { FALL, BIOMETRIC, BEACON }

// 위험 수준
enum class RiskLevel { NONE, LOW, HIGH }

// 상태 (정상, 위험, 권한없음 등)
enum class DetectionStatus { NORMAL, ALERT, NO_PERMISSION, DISCONNECTED }

// 결과 데이터 (Java의 DTO/VO 클래스)
data class DetectionResult(
    val type: DetectorType,
    val status: DetectionStatus,
    val riskLevel: RiskLevel = RiskLevel.NONE,
    val reason: String = ""
)
