package com.example.safetymonitor.coordinator

import com.example.safetymonitor.detection.DetectorType
import com.example.safetymonitor.detection.RiskLevel

/**
 * 공통 판단 설정값.
 *
 * 이 파일에 있는 값은 테스트용 초기값이다.
 * 검증된 의학·안전 기준이 아니며, 실제 운영 전에 반드시 검토·교체해야 한다.
 *
 * ──────────────────────────────────────────────────────────────────────
 * [추후 작업]
 * - 원격 설정(Remote Config) 또는 서버 응답으로 동적 변경 가능하도록 확장
 * - 위험 임계값을 기능별 담당자가 별도 문서로 관리하고 여기에 반영하는 절차 수립
 * ──────────────────────────────────────────────────────────────────────
 */
object AlertConfig {

    /**
     * 기능별 쿨다운 시간 (밀리초).
     *
     * 같은 기능에서 동일 위험 수준의 알림이 이 시간 내에 반복되면 억제한다.
     * 연속 낙상 감지 등 실제 위험 반복 상황에 맞게 조정 필요.
     */
    val cooldownMs: Map<DetectorType, Long> = mapOf(
        DetectorType.FALL      to 10_000L, // 10초 — 낙상은 즉각 재알림이 필요할 수 있어 짧게 설정
        DetectorType.BIOMETRIC to 30_000L, // 30초 — 생체신호는 상태가 지속되므로 길게 설정
        DetectorType.BEACON    to 15_000L  // 15초
    )

    /**
     * 알림을 전송할 최소 위험 수준.
     *
     * LOW  → LOW, HIGH 모두 전송
     * HIGH → HIGH만 전송
     *
     * 현재는 LOW부터 전송하여 테스트를 쉽게 한다.
     * 실제 운영 시 HIGH로 올리거나 기능별로 분리 가능.
     */
    val minimumAlertRiskLevel: RiskLevel = RiskLevel.LOW

    /**
     * 작업자 ID (테스트용 하드코딩 값).
     *
     * 실제 구현 시: 로그인 시스템에서 주입받거나
     * SharedPreferences / DataStore에서 읽어야 한다.
     */
    const val WORKER_ID = "WORKER-TEST-001"
}
