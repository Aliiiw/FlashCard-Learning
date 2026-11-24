package com.alirahimi.flashcard.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.setStatusBarStyle

@Composable
actual fun SystemBarsAppearance(isDarkTheme: Boolean) {
    SideEffect {
        val style = if (isDarkTheme) UIStatusBarStyleLightContent else UIStatusBarStyleDarkContent
        UIApplication.sharedApplication.setStatusBarStyle(style, animated = true)
    }
}
