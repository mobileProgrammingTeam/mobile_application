package com.example.safetymonitor.network

import android.util.Log
import com.example.safetymonitor.detection.DetectionResult
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

interface AlertSender {
    fun send(result: DetectionResult)
}

/**
 * 로컬호스트로 JSON 데이터를 HTTP POST 전송하는 발송기
 *
 * 💡 참고: 안드로이드 에뮬레이터에서 PC의 localhost(127.0.0.1)에 접속할 때는
 * 10.0.2.2 라는 특별한 가상 IP를 사용합니다.
 */
class HttpAlertSender(
    private val serverUrl: String = "http://10.0.2.2:8080/alert"
) : AlertSender {

    override fun send(result: DetectionResult) {
        // 안드로이드에서는 네트워크 작업을 반드시 백그라운드 스레드에서 해야 합니다 (Java의 Thread와 동일)
        thread {
            try {
                // 1. 보낼 JSON 데이터 만들기
                val json = JSONObject().apply {
                    put("workerId", result.workerId)
                    put("dangerType", result.type.name)
                    put("timestamp", result.timestamp)
                    put("message", result.message)
                }

                Log.d("HttpAlertSender", "전송 시도 JSON: $json")

                // 2. HTTP 연결 설정
                val url = URL(serverUrl)
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                    connectTimeout = 3000
                    readTimeout = 3000
                    doOutput = true
                }

                // 3. 데이터 쓰기
                OutputStreamWriter(conn.outputStream, "UTF-8").use { writer ->
                    writer.write(json.toString())
                    writer.flush()
                }

                // 4. 응답 코드 확인
                val responseCode = conn.responseCode
                Log.d("HttpAlertSender", "서버 응답 코드: $responseCode")
                conn.disconnect()

            } catch (e: Exception) {
                // 아직 로컬 서버를 안 띄워놨어도 앱이 꺼지지 않고 로그만 남깁니다.
                Log.w("HttpAlertSender", "서버 전송 실패(서버 미실행 등): ${e.message}")
            }
        }
    }
}
