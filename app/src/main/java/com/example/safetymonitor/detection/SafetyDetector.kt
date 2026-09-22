package com.example.safetymonitor.detection

import kotlinx.coroutines.flow.Flow

/**
 * 모든 감지 기능이 구현해야 하는 공통 인터페이스.
 *
 * ─────────────────────────────────────────────────────────
 * [구현 클래스(Detector)의 책임]
 * 1. 자신이 담당하는 센서·장치에서 데이터를 수집한다.
 * 2. 수집한 데이터를 기반으로 ALERT/NORMAL/오류 상태의
 *    DetectionResult를 판단하여 [results]로 내보낸다.
 * 3. 권한 미허용(NO_PERMISSION), 장치 미연결(DEVICE_DISCONNECTED),
 *    하드웨어 불가(UNAVAILABLE) 등 운영 오류를 DetectionStatus로 구분한다.
 * 4. [start]/[stop] 호출 시 리소스를 적절히 획득·해제한다.
 *
 * [구현 클래스가 하지 않아야 할 것]
 * - 알림 전송 여부 결정 (→ SafetyMonitorCoordinator 책임)
 * - 중복 억제 (→ SafetyMonitorCoordinator 책임)
 * - 서버 통신 (→ AlertSender 책임)
 * ─────────────────────────────────────────────────────────
 *
 * [Coordinator의 책임]
 * - results Flow를 구독하고 중복 억제·임계값 필터링 후 AlertSender를 호출한다.
 */
interface SafetyDetector {

    /**
     * 이 감지기의 고유 식별자.
     * DetectorRegistry에서 등록 및 중복 확인에 사용된다.
     */
    val type: DetectorType

    /**
     * 감지 결과 스트림.
     *
     * - Cold Stream이어야 한다 (collect 시작 후에야 센서 사용).
     * - 에러는 emit하지 않고 DetectionStatus로 전달한다
     *   (Flow 에러로 스트림이 종료되지 않게 하기 위함).
     * - start()가 호출된 이후에만 의미 있는 값이 흐른다.
     *
     * 참고: SharedFlow vs StateFlow 선택 기준
     * https://developer.android.com/kotlin/flow/stateflow-and-sharedflow
     */
    val results: Flow<DetectionResult>

    /**
     * 감지를 시작한다.
     *
     * 구현 예시:
     * - 낙상: SensorManager.registerListener()
     * - 생체신호: Samsung Health SDK 세션 시작 / Wear OS DataClient 연결
     * - 비콘: BluetoothLeScanner.startScan()
     */
    fun start()

    /**
     * 감지를 중지한다.
     *
     * start()에서 획득한 모든 리소스를 반드시 해제해야 한다.
     * - 센서 리스너 해제
     * - BLE 스캔 중지
     * - 외부 SDK 세션 종료
     */
    fun stop()
}
