package com.example.safetymonitor

import com.example.safetymonitor.detection.SafetyDetector
import com.example.safetymonitor.detection.beacon.BeaconDetector
import com.example.safetymonitor.detection.biometric.BiometricDetector
import com.example.safetymonitor.detection.fall.FallDetector
import com.example.safetymonitor.network.AlertSender
import com.example.safetymonitor.network.MockAlertSender

/**
 * 기능 등록 파일.
 *
 * ─────────────────────────────────────────────────────────────────────
 * [팀원 통합 가이드]
 *
 * 각 기능 담당자는 자신의 Detector 구현체를 완성한 뒤
 * 아래 [detectors] 목록에 추가하기만 하면 전체 흐름에 통합된다.
 * 다른 파일(Coordinator, MainActivity 등)은 수정하지 않아도 된다.
 *
 * 실제 서버 AlertSender가 준비되면 [alertSender]의 구현체를 교체한다.
 * ─────────────────────────────────────────────────────────────────────
 *
 * 예시 — 새 Detector 추가:
 *   // 1. detection/gas/ 디렉토리에 GasDetector.kt 생성 (SafetyDetector 구현)
 *   // 2. DetectorType enum에 GAS 항목 추가
 *   // 3. 아래 목록에 추가:
 *   GasDetector(),
 */
object DetectorRegistry {

    /**
     * 등록된 감지 기능 목록.
     *
     * [담당 팀원별 수정 범위]
     * - 낙상 감지 팀원  : FallDetector() 인스턴스를 실제 구현체로 교체
     * - 생체신호 팀원   : BiometricDetector() 인스턴스를 실제 구현체로 교체
     * - 비콘 감지 팀원  : BeaconDetector() 인스턴스를 실제 구현체로 교체
     *
     * Context가 필요한 경우:
     *   FallDetector(context) 형태로 생성자에 전달한다.
     *   context는 provideDetectors(context: Context) 로 받아올 수 있다.
     */
    fun provideDetectors(): List<SafetyDetector> = listOf(
        FallDetector(),
        BiometricDetector(),
        BeaconDetector()
    )

    /**
     * 알림 전송 구현체.
     *
     * 서버 API가 준비되면 MockAlertSender() → HttpAlertSender() 로 교체한다.
     * HttpAlertSender는 AlertSender 인터페이스를 구현하면 된다.
     */
    fun provideAlertSender(): AlertSender = MockAlertSender()
}
