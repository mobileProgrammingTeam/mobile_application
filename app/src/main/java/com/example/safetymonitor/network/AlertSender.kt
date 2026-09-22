package com.example.safetymonitor.network

/**
 * 알림 전송 인터페이스.
 *
 * 서버 주소·인증 방식·API 규격이 확정되면 이 인터페이스를 구현하는
 * 실제 전송 클래스(HttpAlertSender 등)를 만들어 교체한다.
 * MockAlertSender는 테스트 및 개발 중 사용한다.
 *
 * ──────────────────────────────────────────────────────────────────────
 * [실제 구현 시 선택지]
 * - Retrofit + OkHttp : REST API 전송
 * - Ktor Client       : Kotlin-native HTTP 클라이언트
 * - Firebase RTDB     : 실시간 DB (빠른 프로토타입에 적합)
 *
 * 인터페이스만 교체하면 Coordinator 코드는 수정하지 않아도 된다.
 * ──────────────────────────────────────────────────────────────────────
 */
interface AlertSender {

    /**
     * 알림 데이터를 서버(또는 Mock)로 전송한다.
     *
     * suspend 함수이므로 코루틴 컨텍스트에서 호출한다.
     * 전송 실패 시 예외를 던지거나 false를 반환한다.
     *
     * @param payload 전송할 알림 데이터
     * @return 전송 성공 여부
     */
    suspend fun send(payload: AlertPayload): Boolean
}
