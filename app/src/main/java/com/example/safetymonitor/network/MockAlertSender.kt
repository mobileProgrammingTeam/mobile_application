package com.example.safetymonitor.network

import android.util.Log

/**
 * 테스트용 Mock AlertSender.
 *
 * 실제 서버 전송 대신 Android Logcat에 알림 내용을 출력한다.
 * 서버 API가 준비되면 이 클래스를 HttpAlertSender 등으로 교체한다.
 * DetectorRegistry에서 AlertSender 구현체를 교체하면 된다.
 */
class MockAlertSender : AlertSender {

    override suspend fun send(payload: AlertPayload): Boolean {
        // 실제 서버 전송 대신 Logcat에 출력
        Log.w(
            TAG,
            buildString {
                appendLine("═══════════════════════════════════")
                appendLine("🚨 [MOCK 알림 전송]")
                appendLine("  작업자 ID : ${payload.workerId}")
                appendLine("  감지 종류 : ${payload.detectorType.name}")
                appendLine("  위험 수준 : ${payload.riskLevel.name}")
                appendLine("  발생 시각 : ${payload.timestamp}")
                appendLine("  판단 근거 : ${payload.reason}")
                if (payload.extras.isNotEmpty()) {
                    appendLine("  추가 데이터: ${payload.extras}")
                }
                append("═══════════════════════════════════")
            }
        )

        // 전송 성공으로 간주
        return true
    }

    companion object {
        private const val TAG = "MockAlertSender"
    }
}
