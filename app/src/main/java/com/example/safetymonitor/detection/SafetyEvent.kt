package com.example.safetymonitor.detection

// 감지 종류 (팀 아키텍처 명세)
enum class DetectorType { FALL, BEACON, VITAL }

// 감지 상태
enum class DetectionStatus { NORMAL, ALERT }

// 감지 이벤트 모델 (팀 아키텍처 명세)
data class SafetyEvent(
    val workerId: String = "WORKER_01",              // 작업자 ID
    val type: DetectorType,                          // 감지 종류 (FALL, BEACON, VITAL)
    val timestamp: Long = System.currentTimeMillis(), // 발생 시각
    val status: DetectionStatus,                     // 상태 (ALERT, NORMAL)
    val message: String = ""                         // 상세 내용
)
