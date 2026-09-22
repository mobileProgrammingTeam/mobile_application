package com.example.safetymonitor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safetymonitor.detection.DetectionResult
import com.example.safetymonitor.detection.DetectionStatus
import com.example.safetymonitor.detection.DetectorType
import com.example.safetymonitor.detection.RiskLevel
import com.example.safetymonitor.ui.theme.*

/**
 * 메인 테스트 화면.
 *
 * [화면 구성]
 * 1. 헤더 — 모니터링 시작/중지 버튼
 * 2. 상태 카드 — 기능별 마지막 감지 상태
 * 3. 테스트 버튼 패널 — Mock 이벤트 발생 (실제 구현 시 제거)
 * 4. 이벤트 로그 — 최근 감지 결과 20개
 */
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val state by viewModel.uiState.collectAsState()

    SafetyMonitorTheme {
        Scaffold(
            topBar = { AppTopBar() }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(Modifier.height(4.dp))

                // 모니터링 토글 버튼
                MonitoringToggleButton(
                    isMonitoring = state.isMonitoring,
                    onStart = viewModel::startMonitoring,
                    onStop  = viewModel::stopMonitoring
                )

                // 기능별 상태 카드
                StatusSection(latestResults = state.latestResults)

                // 테스트 버튼 패널
                TestButtonPanel(viewModel = viewModel, enabled = state.isMonitoring)

                // 이벤트 로그
                EventLogSection(log = state.eventLog)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar() {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Security,
                    contentDescription = null,
                    tint = PrimaryLight,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "작업자 안전 모니터",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun MonitoringToggleButton(
    isMonitoring: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    Button(
        onClick = if (isMonitoring) onStop else onStart,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isMonitoring) DangerRed else SafeGreen
        )
    ) {
        Icon(
            imageVector = if (isMonitoring) Icons.Filled.Stop else Icons.Filled.PlayArrow,
            contentDescription = null
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (isMonitoring) "모니터링 중지" else "모니터링 시작",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun StatusSection(latestResults: Map<DetectorType, DetectionResult>) {
    Text(
        text = "감지 상태",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DetectorType.entries.forEach { type ->
            StatusCard(
                type   = type,
                result = latestResults[type],
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatusCard(
    type: DetectorType,
    result: DetectionResult?,
    modifier: Modifier = Modifier
) {
    val (statusColor, statusText, icon) = when {
        result == null -> Triple(NeutralGray, "대기", Icons.Filled.HourglassEmpty)
        result.status == DetectionStatus.ALERT && result.riskLevel == RiskLevel.HIGH ->
            Triple(DangerRed, "위험", Icons.Filled.Warning)
        result.status == DetectionStatus.ALERT && result.riskLevel == RiskLevel.LOW ->
            Triple(WarningAmber, "주의", Icons.Filled.NotificationImportant)
        result.status == DetectionStatus.NORMAL ->
            Triple(SafeGreen, "정상", Icons.Filled.CheckCircle)
        result.status == DetectionStatus.NO_PERMISSION ->
            Triple(WarningAmber, "권한 필요", Icons.Filled.Lock)
        result.status == DetectionStatus.DEVICE_DISCONNECTED ->
            Triple(WarningAmber, "미연결", Icons.Filled.BluetoothDisabled)
        else -> Triple(NeutralGray, "불가", Icons.Filled.SignalCellularOff)
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = type.displayName.replace(" ", "\n"),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelSmall,
                color = statusColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TestButtonPanel(viewModel: MainViewModel, enabled: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🧪 테스트 이벤트 (Mock)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "모니터링 시작 후 버튼을 눌러 흐름을 확인하세요.\n실제 구현 시 이 패널은 제거합니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TestButton("🤸 낙상", enabled, Modifier.weight(1f)) { viewModel.triggerFallAlert() }
                TestButton("❤️ 생체", enabled, Modifier.weight(1f)) { viewModel.triggerBiometricAlert() }
                TestButton("📡 비콘", enabled, Modifier.weight(1f)) { viewModel.triggerBeaconAlert() }
            }
            OutlinedButton(
                onClick = viewModel::triggerFallNormal,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("정상 이벤트 발생 (낙상 → NORMAL)", fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun TestButton(
    label: String,
    enabled: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
    ) {
        Text(label, fontSize = 12.sp, maxLines = 1)
    }
}

@Composable
private fun EventLogSection(log: List<String>) {
    Text(
        text = "이벤트 로그",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
    Card(
        modifier = Modifier.fillMaxWidth().weight(1f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        if (log.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "모니터링을 시작하면 이벤트가 표시됩니다.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(log) { entry ->
                    Text(
                        text = entry,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        ),
                        color = when {
                            "ALERT" in entry && "HIGH" in entry -> DangerRed
                            "ALERT" in entry -> WarningAmber
                            "NORMAL" in entry -> SafeGreen
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 0.5.dp)
                }
            }
        }
    }
    Spacer(Modifier.height(8.dp))
}
