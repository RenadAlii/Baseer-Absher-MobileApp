package com.baseer.baseer

import androidx.compose.ui.window.ComposeUIViewController
import com.baseer.baseer.di.appModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoinIos()
    }
) { App() }

fun initKoinIos() {
    startKoin {
        modules(appModule)
    }
}