package com.example.safetymonitor.detection

// 감지 종류
enum class DetectorType { FALL, BIOMETRIC, BEACON }

// 감지 상태 (위험 감지 여부)
enum class DetectionStatus { NORMAL, ALERT }

// 결과 데이터 (Java의 DTO/VO 클래스)
data class DetectionResult(
    val workerId: String = "WORKER_01",              // 작업자 ID (임의 설정)
    val type: DetectorType,                          // 위험 타입 (FALL, BIOMETRIC, BEACON)
    val timestamp: Long = System.currentTimeMillis(), // 발생 시간 (밀리초)
    val status: DetectionStatus,                     // 상태 (ALERT, NORMAL)
    val message: String = ""                         // 상세 메시지
)
