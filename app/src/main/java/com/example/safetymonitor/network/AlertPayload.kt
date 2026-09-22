package com.example.safetymonitor.network

import com.example.safetymonitor.detection.DetectorType
import com.example.safetymonitor.detection.RiskLevel
import java.time.Instant

/**
 * 서버로 전송할 알림 데이터 모델.
 *
 * 서버 API 규격이 확정되면 필드명을 맞추거나
 * 직렬화 어노테이션(@Json, @SerialName)을 추가한다.
 *
 * @param workerId      작업자 식별자
 * @param detectorType  어떤 감지 기능에서 발생했는지
 * @param timestamp     감지 발생 시각 (UTC)
 * @param riskLevel     위험 수준
 * @param reason        판단 근거 (사람이 읽을 수 있는 문자열)
 * @param extras        기능별 원시 측정값 등 추가 정보
 */
data class AlertPayload(
    val workerId: String,
    val detectorType: DetectorType,
    val timestamp: Instant,
    val riskLevel: RiskLevel,
    val reason: String,
    val extras: Map<String, Any> = emptyMap()
)
