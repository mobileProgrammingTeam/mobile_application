package com.example.safetymonitor.detection.biometric

import android.util.Log
import com.example.safetymonitor.detection.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 생체신호 감지 Mock 구현체 (갤럭시 워치 연동 예정).
 *
 * ─────────────────────────────────────────────────────────────────────
 * [담당 팀원 작업 안내]
 *
 * 갤럭시 워치 데이터 수신 방식이 확정되면 이 파일에 실제 로직을 구현한다.
 *
 * ★ 워치 연동 방식은 아직 미확정. 아래 두 가지 경로 중 하나를 선택한다:
 *
 * 방법 A — Samsung Health SDK (Galaxy Watch 전용)
 *   - Samsung Developer 등록 및 SDK 다운로드 필요
 *   - HealthTrackingService로 HeartRateTracker, StressTracker 등 구독
 *   - 참고: https://developer.samsung.com/health/android/data/guide/health-tracking.html
 *   - 주의: 삼성 단말·워치에서만 동작
 *
 * 방법 B — Wear OS Health Services + Data Layer API (범용)
 *   - 워치에서 Health Services로 측정 → DataClient로 폰에 전달
 *   - 참고: https://developer.android.com/training/wearables/health-services
 *   - 참고: https://developer.android.com/training/wearables/data/data-items
 *
 * 방법 C — BLE GATT 직접 통신 (커스텀)
 *   - 워치 앱이 BLE GATT 서버 역할, 폰이 클라이언트로 연결
 *
 * 방법이 확정되면 아래 주석 처리된 TODO를 실제 코드로 교체한다.
 *
 * 필요 권한 (방법에 따라 다름):
 *   - Samsung Health SDK: BODY_SENSORS
 *   - Wear OS: BODY_SENSORS (워치), DATA_LAYER API (폰 측 별도 권한 없음)
 *   - BLE: BLUETOOTH_CONNECT, BLUETOOTH_SCAN
 * ─────────────────────────────────────────────────────────────────────
 */
class BiometricDetector : SafetyDetector {

    override val type = DetectorType.BIOMETRIC

    private val _results = MutableSharedFlow<DetectionResult>(extraBufferCapacity = 16)
    override val results: Flow<DetectionResult> = _results.asSharedFlow()

    private var running = false

    override fun start() {
        if (running) return
        running = true
        Log.d(TAG, "BiometricDetector 시작 (Mock 모드 — 워치 연동 미구현)")
        // TODO: 방법 확정 후 SDK 초기화 / DataClient 리스너 등록
    }

    override fun stop() {
        if (!running) return
        running = false
        Log.d(TAG, "BiometricDetector 중지")
        // TODO: SDK 세션 종료 / DataClient 리스너 해제
    }

    // ─── 테스트 전용 함수 ─────────────────────────────────────────────

    /** 테스트용 비정상 심박수 이벤트 */
    suspend fun simulateAbnormalHeartRate(bpm: Int = 145) {
        val risk = when {
            bpm >= HIGH_RISK_BPM -> RiskLevel.HIGH
            bpm >= LOW_RISK_BPM  -> RiskLevel.LOW
            else                 -> RiskLevel.NONE
        }
        val status = if (risk != RiskLevel.NONE) DetectionStatus.ALERT else DetectionStatus.NORMAL
        _results.emit(
            DetectionResult(
                type      = DetectorType.BIOMETRIC,
                status    = status,
                riskLevel = risk,
                reason    = "심박수 비정상: ${bpm}bpm",
                extras    = mapOf("heartRateBpm" to bpm)
            )
        )
    }

    /** 테스트용 정상 생체신호 이벤트 */
    suspend fun simulateNormal(bpm: Int = 72) {
        _results.emit(
            DetectionResult(
                type      = DetectorType.BIOMETRIC,
                status    = DetectionStatus.NORMAL,
                riskLevel = RiskLevel.NONE,
                reason    = "정상 심박수: ${bpm}bpm",
                extras    = mapOf("heartRateBpm" to bpm)
            )
        )
    }

    /** 테스트용 워치 미연결 이벤트 */
    suspend fun simulateDeviceDisconnected() {
        _results.emit(
            DetectionResult(
                type      = DetectorType.BIOMETRIC,
                status    = DetectionStatus.DEVICE_DISCONNECTED,
                riskLevel = RiskLevel.NONE,
                reason    = "갤럭시 워치 미연결 (시뮬레이션)"
            )
        )
    }

    companion object {
        private const val TAG = "BiometricDetector"

        /**
         * 심박수 위험 임계값 (테스트용).
         * 실제 의학적 기준이 아니며, 구현 시 적절한 기준으로 교체해야 한다.
         */
        const val HIGH_RISK_BPM = 140
        const val LOW_RISK_BPM  = 110
    }
}
