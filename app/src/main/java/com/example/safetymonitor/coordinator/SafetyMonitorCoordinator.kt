package com.example.safetymonitor.coordinator

import android.util.Log
import com.example.safetymonitor.detection.DetectionResult
import com.example.safetymonitor.detection.DetectorType
import com.example.safetymonitor.detection.RiskLevel
import com.example.safetymonitor.detection.SafetyDetector
import com.example.safetymonitor.network.AlertPayload
import com.example.safetymonitor.network.AlertSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * 공통 판단 객체 (Safety Monitor Coordinator).
 *
 * ─────────────────────────────────────────────────────────────────────
 * [이 클래스의 책임]
 * 1. 등록된 모든 SafetyDetector의 results Flow를 구독한다.
 * 2. 결과가 알림 전송 조건(isAlert, minimumRiskLevel)을 충족하는지 판단한다.
 * 3. 쿨다운(cooldown)을 적용하여 같은 기능의 반복 알림을 억제한다.
 * 4. 조건을 충족하면 AlertSender.send()를 호출한다.
 * 5. 모든 감지 결과를 UI에서 관찰할 수 있는 SharedFlow로 재방출한다.
 *
 * [이 클래스가 하지 않는 것]
 * - 실제 센서 데이터 수집 (→ 각 Detector 책임)
 * - 위험 판단 (ALERT/NORMAL 결정은 Detector가 함)
 * - 서버 통신 로직 (→ AlertSender 책임)
 * ─────────────────────────────────────────────────────────────────────
 *
 * @param detectors  감지 기능 목록 (DetectorRegistry에서 주입)
 * @param alertSender 알림 전송 구현체 (Mock 또는 실제)
 * @param scope      코루틴 스코프 (ViewModel의 viewModelScope 사용 권장)
 */
class SafetyMonitorCoordinator(
    private val detectors: List<SafetyDetector>,
    private val alertSender: AlertSender,
    private val scope: CoroutineScope
) {

    // UI에서 관찰할 수 있는 모든 감지 결과 스트림
    private val _allResults = MutableSharedFlow<DetectionResult>(extraBufferCapacity = 32)
    val allResults: SharedFlow<DetectionResult> = _allResults.asSharedFlow()

    // 기능별 마지막 알림 전송 시각 (쿨다운 추적용)
    // key: DetectorType, value: System.currentTimeMillis()
    private val lastAlertSentAt = mutableMapOf<DetectorType, Long>()

    // 기능별 구독 Job (stop() 시 취소)
    private val jobs = mutableMapOf<DetectorType, Job>()

    /**
     * 모든 감지 기능을 시작하고 결과 구독을 시작한다.
     * ViewModel의 init 또는 화면 진입 시 호출한다.
     */
    fun startAll() {
        detectors.forEach { detector ->
            detector.start()
            jobs[detector.type] = scope.launch {
                detector.results.collect { result ->
                    handleResult(result)
                }
            }
        }
        Log.d(TAG, "전체 감지 시작: ${detectors.map { it.type.name }}")
    }

    /**
     * 모든 감지 기능을 중지하고 구독을 취소한다.
     * ViewModel의 onCleared() 또는 화면 종료 시 호출한다.
     */
    fun stopAll() {
        jobs.values.forEach { it.cancel() }
        jobs.clear()
        detectors.forEach { it.stop() }
        Log.d(TAG, "전체 감지 중지")
    }

    /**
     * 개별 감지 결과 처리.
     *
     * 판단 흐름:
     *   1. UI 스트림으로 재방출 (상태와 무관하게 항상)
     *   2. isAlert 확인 (ALERT + riskLevel != NONE)
     *   3. minimumAlertRiskLevel 확인
     *   4. 쿨다운 확인 (기능별 마지막 전송 시각 비교)
     *   5. AlertSender.send() 호출
     */
    private suspend fun handleResult(result: DetectionResult) {
        // 1. UI에 무조건 전달 (오류 상태도 화면에 표시)
        _allResults.emit(result)

        // 2. 알림 조건 미충족 → 종료
        if (!result.isAlert) {
            Log.v(TAG, "[${result.type.name}] 알림 불필요: ${result.status}")
            return
        }

        // 3. 최소 위험 수준 미달 → 종료
        if (!meetsMinimumRiskLevel(result.riskLevel)) {
            Log.d(TAG, "[${result.type.name}] 위험 수준 미달: ${result.riskLevel}")
            return
        }

        // 4. 쿨다운 확인
        val now = System.currentTimeMillis()
        val cooldown = AlertConfig.cooldownMs[result.type] ?: 10_000L
        val lastSent = lastAlertSentAt[result.type] ?: 0L
        if (now - lastSent < cooldown) {
            val remainMs = cooldown - (now - lastSent)
            Log.d(TAG, "[${result.type.name}] 쿨다운 중 (${remainMs}ms 남음) — 알림 억제")
            return
        }

        // 5. 알림 전송
        val payload = AlertPayload(
            workerId     = AlertConfig.WORKER_ID,
            detectorType = result.type,
            timestamp    = result.timestamp,
            riskLevel    = result.riskLevel,
            reason       = result.reason,
            extras       = result.extras
        )

        val success = runCatching { alertSender.send(payload) }.getOrElse { e ->
            Log.e(TAG, "알림 전송 실패: ${e.message}")
            false
        }

        if (success) {
            lastAlertSentAt[result.type] = now
            Log.i(TAG, "[${result.type.name}] 알림 전송 완료")
        }
    }

    private fun meetsMinimumRiskLevel(level: RiskLevel): Boolean {
        return when (AlertConfig.minimumAlertRiskLevel) {
            RiskLevel.NONE -> true
            RiskLevel.LOW  -> level == RiskLevel.LOW || level == RiskLevel.HIGH
            RiskLevel.HIGH -> level == RiskLevel.HIGH
        }
    }

    companion object {
        private const val TAG = "SafetyCoordinator"
    }
}
