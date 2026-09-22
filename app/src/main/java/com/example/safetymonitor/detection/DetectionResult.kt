package com.example.safetymonitor.detection

import java.time.Instant

// =============================================================================
// 감지 기능 종류
// =============================================================================

/**
 * 감지 기능 종류.
 *
 * 새 기능을 추가하는 팀원은 이 enum에 항목을 추가한다.
 * DetectorRegistry에도 해당 구현체를 등록해야 한다.
 */
enum class DetectorType(
    /** 화면·로그에 표시할 사람이 읽을 수 있는 이름 */
    val displayName: String
) {
    FALL("낙상 감지"),          // 스마트폰 가속도 센서
    BIOMETRIC("생체신호 감지"), // 갤럭시 워치
    BEACON("위험구역 접근")    // BLE 비콘
}

// =============================================================================
// 위험 수준
// =============================================================================

/**
 * 위험 수준.
 *
 * - [NONE]   정상 / 위험 없음
 * - [LOW]    주의 필요. 경고 로그만 남기고 알림은 상황에 따라 결정.
 * - [HIGH]   즉각 알림이 필요한 위험 상황.
 */
enum class RiskLevel { NONE, LOW, HIGH }

// =============================================================================
// 감지 결과 상태
// =============================================================================

/**
 * 감지 결과의 운영 상태.
 *
 * 구현 클래스는 실제 위험이 아닌 경우에도 적절한 상태를 반드시 전달해
 * Coordinator가 불필요한 알림을 보내지 않도록 해야 한다.
 *
 * | 상태                  | 의미                                           |
 * |-----------------------|------------------------------------------------|
 * | NORMAL                | 정상 측정, 위험 없음                           |
 * | ALERT                 | 위험 감지됨 — 알림 전송 후보                   |
 * | NO_PERMISSION         | 필요한 Android 권한이 허용되지 않음            |
 * | DEVICE_DISCONNECTED   | 외부 장치(워치, 비콘 스캐너 등)가 연결되지 않음|
 * | UNAVAILABLE           | 센서·하드웨어가 이 기기에서 사용 불가          |
 */
enum class DetectionStatus {
    NORMAL,
    ALERT,
    NO_PERMISSION,
    DEVICE_DISCONNECTED,
    UNAVAILABLE
}

// =============================================================================
// 감지 결과 데이터 모델
// =============================================================================

/**
 * 모든 감지 기능이 공통으로 사용하는 결과 데이터 모델.
 *
 * 구현 클래스(FallDetector, BiometricDetector, BeaconDetector)는
 * 이 data class 인스턴스를 Flow로 내보낸다.
 *
 * @param type      어떤 기능이 생성한 결과인지 ([DetectorType])
 * @param status    결과 상태 — 위험 여부 또는 운영 오류 종류
 * @param riskLevel 위험 수준 (ALERT 상태가 아니면 반드시 NONE)
 * @param reason    판단 근거 — 로그·알림에 표시될 사람이 읽을 수 있는 문자열
 *                  예: "낙상 의심: G-force 4.2g 감지", "심박수 비정상: 142bpm"
 * @param timestamp 감지 발생 시각 (UTC Instant)
 * @param extras    기능별 원시 측정값 등 추가 데이터 (선택)
 *                  예: mapOf("gForce" to 4.2f, "heartRate" to 142)
 */
data class DetectionResult(
    val type: DetectorType,
    val status: DetectionStatus,
    val riskLevel: RiskLevel = RiskLevel.NONE,
    val reason: String = "",
    val timestamp: Instant = Instant.now(),
    val extras: Map<String, Any> = emptyMap()
) {
    /**
     * 알림 전송 후보 여부.
     *
     * ALERT 상태이고 riskLevel이 NONE이 아닌 경우에만 true.
     * Coordinator는 이 프로퍼티를 1차 필터로 사용한다.
     */
    val isAlert: Boolean
        get() = status == DetectionStatus.ALERT && riskLevel != RiskLevel.NONE
}
