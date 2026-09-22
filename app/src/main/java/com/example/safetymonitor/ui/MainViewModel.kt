package com.example.safetymonitor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetymonitor.DetectorRegistry
import com.example.safetymonitor.coordinator.SafetyMonitorCoordinator
import com.example.safetymonitor.detection.DetectionResult
import com.example.safetymonitor.detection.DetectorType
import com.example.safetymonitor.detection.beacon.BeaconDetector
import com.example.safetymonitor.detection.biometric.BiometricDetector
import com.example.safetymonitor.detection.fall.FallDetector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MainScreen 전용 ViewModel.
 *
 * [이 클래스의 책임]
 * - Coordinator를 생성하고 생명주기에 맞게 start/stop을 관리한다.
 * - UI 상태([UiState])를 유지하고 Compose에 제공한다.
 * - 테스트 버튼 이벤트를 각 Mock Detector의 simulate 함수로 전달한다.
 *
 * [추후 작업]
 * - Hilt 또는 수동 ViewModelFactory로 Context를 주입하면
 *   실제 Detector(SensorManager, BLE 등)를 사용할 수 있다.
 * - 현재는 Context 없이 동작하는 Mock Detector만 사용한다.
 */
class MainViewModel : ViewModel() {

    // 등록된 Detector 인스턴스를 보관 (simulate 함수 호출용)
    private val detectors = DetectorRegistry.provideDetectors()

    private val coordinator = SafetyMonitorCoordinator(
        detectors   = detectors,
        alertSender = DetectorRegistry.provideAlertSender(),
        scope       = viewModelScope
    )

    // ─── UI 상태 ────────────────────────────────────────────────────

    data class UiState(
        val isMonitoring: Boolean = false,
        /** 기능별 마지막 감지 결과 (화면에 현재 상태 표시용) */
        val latestResults: Map<DetectorType, DetectionResult> = emptyMap(),
        /** 감지 이벤트 로그 (최신 20개) */
        val eventLog: List<String> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        // Coordinator의 allResults를 관찰하여 UI 상태 갱신
        viewModelScope.launch {
            coordinator.allResults.collect { result ->
                _uiState.update { state ->
                    val newResults = state.latestResults.toMutableMap()
                    newResults[result.type] = result

                    val logEntry = "[${result.type.displayName}] ${result.status.name}" +
                        if (result.reason.isNotBlank()) " — ${result.reason}" else ""
                    val newLog = (listOf(logEntry) + state.eventLog).take(20)

                    state.copy(latestResults = newResults, eventLog = newLog)
                }
            }
        }
    }

    // ─── 제어 함수 ──────────────────────────────────────────────────

    fun startMonitoring() {
        coordinator.startAll()
        _uiState.update { it.copy(isMonitoring = true) }
    }

    fun stopMonitoring() {
        coordinator.stopAll()
        _uiState.update { it.copy(isMonitoring = false) }
    }

    override fun onCleared() {
        super.onCleared()
        coordinator.stopAll()
    }

    // ─── 테스트 이벤트 발생 함수 ────────────────────────────────────
    // 실제 구현 시 이 함수들은 삭제하고, 버튼도 테스트 빌드에서만 표시하도록 변경한다.

    fun triggerFallAlert() = viewModelScope.launch {
        (detectors.find { it.type == DetectorType.FALL } as? FallDetector)
            ?.simulateFall()
    }

    fun triggerBiometricAlert() = viewModelScope.launch {
        (detectors.find { it.type == DetectorType.BIOMETRIC } as? BiometricDetector)
            ?.simulateAbnormalHeartRate()
    }

    fun triggerBeaconAlert() = viewModelScope.launch {
        (detectors.find { it.type == DetectorType.BEACON } as? BeaconDetector)
            ?.simulateZoneEntry()
    }

    fun triggerFallNormal() = viewModelScope.launch {
        (detectors.find { it.type == DetectorType.FALL } as? FallDetector)
            ?.simulateNormal()
    }
}
