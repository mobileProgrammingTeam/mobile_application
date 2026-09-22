package com.example.safetymonitor.network

import android.util.Log
import com.example.safetymonitor.detection.DetectionResult

// 서버 전송 인터페이스
interface AlertSender {
    fun send(result: DetectionResult)
}

// 테스트용 가짜 전송기 (콘솔 로그만 출력)
class MockAlertSender : AlertSender {
    override fun send(result: DetectionResult) {
        Log.d("AlertSender", "🚨 [서버 전송] ${result.type} - ${result.reason}")
    }
}
