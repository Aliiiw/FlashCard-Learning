package com.alirahimi.flashcard

import androidx.compose.ui.window.ComposeUIViewController
import com.alirahimi.flashcard.di.initKoin
import org.koin.core.context.GlobalContext

fun MainViewController() = ComposeUIViewController {
    if (GlobalContext.getOrNull() == null) {
        initKoin()
    }
    App()
}