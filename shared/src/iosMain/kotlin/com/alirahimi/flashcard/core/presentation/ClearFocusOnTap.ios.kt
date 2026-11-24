package com.alirahimi.flashcard.core.presentation

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import platform.UIKit.UIApplication
import platform.UIKit.endEditing

actual fun Modifier.clearFocusOnTap(): Modifier = pointerInput(Unit) {
    detectTapGestures(onTap = {
        UIApplication.sharedApplication.keyWindow?.endEditing(true)
    })
}
