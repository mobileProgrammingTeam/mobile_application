package com.example.safetymonitor.detection.beacon

import android.util.Log
import com.example.safetymonitor.detection.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 비콘 기반 위험구역 접근 감지 Mock 구현체.
 *
 * ─────────────────────────────────────────────────────────────────────
 * [담당 팀원 작업 안내]
 *
 * BLE 비콘 스캔 실제 구현 시 이 파일에 로직을 추가한다.
 *
 * 실제 구현 방법:
 * 1. 생성자에 Context 추가
 * 2. BluetoothManager / BluetoothLeScanner 획득
 *      val scanner = bluetoothAdapter.bluetoothLeScanner
 * 3. start()에서 스캔 시작
 *      scanner.startScan(filters, settings, scanCallback)
 *      참고: https://developer.android.com/guide/topics/connectivity/bluetooth/find-ble-devices
 * 4. ScanCallback.onScanResult()에서 RSSI로 근접 여부 판단
 *      if (result.rssi >= DANGER_RSSI_THRESHOLD) → ALERT 발행
 * 5. stop()에서 scanner.stopScan() 호출
 *
 * 비콘 식별 방법:
 * - iBeacon: UUID/Major/Minor로 위험구역 식별
 * - Eddystone: URL 또는 UID로 구역 식별
 * - 커스텀: 광고 패킷 파싱
 *
 * 필요 권한 (AndroidManifest.xml 주석 참고):
 *   - Android 12+: BLUETOOTH_SCAN (android:usesPermissionFlags="neverForLocation" 추천)
 *   - Android 11-: BLUETOOTH, BLUETOOTH_ADMIN, ACCESS_FINE_LOCATION
 *   참고: https://developer.android.com/guide/topics/connectivity/bluetooth/permissions
 *
 * 백그라운드 BLE 스캔:
 *   - Android 8.0+: 백그라운드 스캔은 30분마다 중지됨
 *   - 지속 스캔이 필요하면 Foreground Service에서 실행해야 함
 *   참고: https://developer.android.com/guide/topics/connectivity/bluetooth/ble-overview#connect-gatt
 * ─────────────────────────────────────────────────────────────────────
 */
class BeaconDetector : SafetyDetector {

    override val type = DetectorType.BEACON

    private val _results = MutableSharedFlow<DetectionResult>(extraBufferCapacity = 16)
    override val results: Flow<DetectionResult> = _results.asSharedFlow()

    private var running = false

    override fun start() {
        if (running) return
        running = true
        Log.d(TAG, "BeaconDetector 시작 (Mock 모드)")
        // TODO: BluetoothLeScanner.startScan() 호출
    }

    override fun stop() {
        if (!running) return
        running = false
        Log.d(TAG, "BeaconDetector 중지")
        // TODO: BluetoothLeScanner.stopScan() 호출
    }

    // ─── 테스트 전용 함수 ─────────────────────────────────────────────

    /**
     * 테스트용 위험구역 접근 이벤트.
     * @param beaconId  접근한 비콘 식별자 (실제 구현 시 UUID 등)
     * @param rssi      신호 강도 (dBm, 클수록 가까움)
     */
    suspend fun simulateZoneEntry(beaconId: String = "DANGER-ZONE-001", rssi: Int = -55) {
        val risk = if (rssi >= HIGH_RISK_RSSI) RiskLevel.HIGH else RiskLevel.LOW
        _results.emit(
            DetectionResult(
                type      = DetectorType.BEACON,
                status    = DetectionStatus.ALERT,
                riskLevel = risk,
                reason    = "위험구역 접근: 비콘 [$beaconId] RSSI ${rssi}dBm",
                extras    = mapOf("beaconId" to beaconId, "rssi" to rssi)
            )
        )
    }

    /** 테스트용 구역 이탈 이벤트 */
    suspend fun simulateZoneExit(beaconId: String = "DANGER-ZONE-001") {
        _results.emit(
            DetectionResult(
                type      = DetectorType.BEACON,
                status    = DetectionStatus.NORMAL,
                riskLevel = RiskLevel.NONE,
                reason    = "안전구역: 비콘 [$beaconId] 범위 밖"
            )
        )
    }

    /** 테스트용 BLE 권한 오류 이벤트 */
    suspend fun simulateNoPermission() {
        _results.emit(
            DetectionResult(
                type      = DetectorType.BEACON,
                status    = DetectionStatus.NO_PERMISSION,
                riskLevel = RiskLevel.NONE,
                reason    = "Bluetooth 권한 미허용 (시뮬레이션)"
            )
        )
    }

    companion object {
        private const val TAG = "BeaconDetector"

        /**
         * HIGH 위험 판정 RSSI 임계값 (테스트용, dBm).
         * -60dBm ≈ 약 3~5m 거리 (환경에 따라 크게 달라짐).
         * 실제 현장 캘리브레이션 후 조정 필요.
         */
        const val HIGH_RISK_RSSI = -60
    }
}
