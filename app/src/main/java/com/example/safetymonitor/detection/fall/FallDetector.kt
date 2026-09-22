package com.example.safetymonitor.detection.fall

import android.util.Log
import com.example.safetymonitor.detection.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 낙상 감지 Mock 구현체.
 *
 * ─────────────────────────────────────────────────────────────────────
 * [담당 팀원 작업 안내]
 *
 * 이 파일을 수정하여 실제 낙상 감지를 구현한다.
 * DetectorRegistry, SafetyDetector 인터페이스는 수정하지 않는다.
 *
 * 실제 구현 시 교체/추가할 내용:
 * 1. 생성자에 Context 추가 (SensorManager 획득용)
 *      class FallDetector(private val context: Context) : SafetyDetector
 *
 * 2. start()에서 SensorManager 등록
 *      val sensorManager = context.getSystemService(SensorManager::class.java)
 *      val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
 *      sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
 *      참고: https://developer.android.com/guide/topics/sensors/sensors_motion
 *
 * 3. SensorEventListener.onSensorChanged()에서 G-force 계산 후 _results.tryEmit()
 *      G = sqrt(x² + y² + z²) / STANDARD_GRAVITY
 *
 * 4. stop()에서 sensorManager.unregisterListener() 호출
 *
 * 5. 백그라운드 동작이 필요한 경우 Foreground Service에서 이 클래스를 사용
 *      참고: https://developer.android.com/guide/components/foreground-services
 *
 * 필요 권한:
 *   - 가속도 센서: 권한 불필요
 *     https://developer.android.com/guide/topics/sensors/sensors_overview#sensors-identify
 *   - 백그라운드 동작: FOREGROUND_SERVICE (추후 작업)
 * ─────────────────────────────────────────────────────────────────────
 */
class FallDetector : SafetyDetector {

    override val type = DetectorType.FALL

    // MutableSharedFlow: 구독자가 없어도 emit 가능, 버퍼 16개
    private val _results = MutableSharedFlow<DetectionResult>(extraBufferCapacity = 16)
    override val results: Flow<DetectionResult> = _results.asSharedFlow()

    private var running = false

    override fun start() {
        if (running) return
        running = true
        Log.d(TAG, "FallDetector 시작 (Mock 모드)")
        // TODO: SensorManager.registerListener() 호출
    }

    override fun stop() {
        if (!running) return
        running = false
        Log.d(TAG, "FallDetector 중지")
        // TODO: SensorManager.unregisterListener() 호출
    }

    // ─── 테스트 전용 함수 ─────────────────────────────────────────────
    // 실제 구현 시에는 내부 SensorEventListener에서 자동으로 emit한다.
    // 이 함수들은 MainScreen의 테스트 버튼에서만 호출된다.

    /**
     * 테스트용 낙상 이벤트 발생.
     * @param gForce 시뮬레이션할 G-force 값 (기본 4.5g)
     */
    suspend fun simulateFall(gForce: Float = 4.5f) {
        val risk = if (gForce >= HIGH_RISK_G_FORCE) RiskLevel.HIGH else RiskLevel.LOW
        _results.emit(
            DetectionResult(
                type      = DetectorType.FALL,
                status    = DetectionStatus.ALERT,
                riskLevel = risk,
                reason    = "낙상 의심: G-force ${"%.1f".format(gForce)}g 감지",
                extras    = mapOf("gForce" to gForce)
            )
        )
    }

    /** 테스트용 정상 이벤트 발생 */
    suspend fun simulateNormal() {
        _results.emit(
            DetectionResult(
                type      = DetectorType.FALL,
                status    = DetectionStatus.NORMAL,
                riskLevel = RiskLevel.NONE,
                reason    = "정상 움직임"
            )
        )
    }

    /** 테스트용 권한 오류 이벤트 */
    suspend fun simulateNoPermission() {
        _results.emit(
            DetectionResult(
                type      = DetectorType.FALL,
                status    = DetectionStatus.NO_PERMISSION,
                riskLevel = RiskLevel.NONE,
                reason    = "센서 권한 미허용 (시뮬레이션)"
            )
        )
    }

    companion object {
        private const val TAG = "FallDetector"

        /**
         * HIGH 위험 판정 G-force 임계값 (테스트용).
         * 검증된 낙상 감지 임계값이 아니므로 실제 구현 시 문헌 기준으로 교체해야 한다.
         * AlertConfig.kt 에서 중앙 관리하도록 이전 가능.
         */
        const val HIGH_RISK_G_FORCE = 3.0f
    }
}
