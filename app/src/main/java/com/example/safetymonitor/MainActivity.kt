package com.example.safetymonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.safetymonitor.ui.MainScreen
import com.example.safetymonitor.ui.MainViewModel

/**
 * 앱의 단일 Activity.
 *
 * [추후 작업]
 * - 실제 감지 기능이 백그라운드에서 동작해야 하면
 *   Activity가 아닌 Foreground Service에서 Coordinator를 실행한다.
 *   Activity는 서비스에 바인딩하여 상태를 받아온다.
 *   참고: https://developer.android.com/guide/components/bound-services
 *
 * - 권한 요청 로직은 각 기능 담당자가 추가한다.
 *   Compose에서는 accompanist-permissions 또는
 *   ActivityResultContracts.RequestMultiplePermissions 사용을 권장한다.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen(viewModel = viewModel)
        }
    }
}
