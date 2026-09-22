package com.example.safetymonitor.network

import android.util.Log
import com.example.safetymonitor.detection.SafetyEvent
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

interface AlertSender {
    fun send(event: SafetyEvent)
}

/**
 * 로컬 FastAPI 백엔드로 HTTP POST 전송하는 발송기
 */
class HttpAlertSender(
    private val serverUrl: String = "http://10.0.2.2:8080/alert"
) : AlertSender {

    override fun send(event: SafetyEvent) {
        thread {
            try {
                val json = JSONObject().apply {
                    put("workerId", event.workerId)
                    put("dangerType", event.type.name)
                    put("timestamp", event.timestamp)
                    put("message", event.message)
                }

                Log.d("HttpAlertSender", "전송 시도 JSON: $json")

                val url = URL(serverUrl)
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                    connectTimeout = 3000
                    readTimeout = 3000
                    doOutput = true
                }

                OutputStreamWriter(conn.outputStream, "UTF-8").use { writer ->
                    writer.write(json.toString())
                    writer.flush()
                }

                val responseCode = conn.responseCode
                Log.d("HttpAlertSender", "서버 응답 코드: $responseCode")
                conn.disconnect()

            } catch (e: Exception) {
                Log.w("HttpAlertSender", "서버 전송 실패(서버 미실행 등): ${e.message}")
            }
        }
    }
}
