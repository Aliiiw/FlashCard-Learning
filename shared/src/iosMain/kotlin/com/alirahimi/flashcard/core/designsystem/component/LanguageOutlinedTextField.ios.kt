package com.alirahimi.flashcard.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSNotificationCenter
import platform.Foundation.setValue
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UITextBorderStyle
import platform.UIKit.UITextField
import platform.UIKit.UITextFieldTextDidBeginEditingNotification
import platform.UIKit.UITextFieldTextDidChangeNotification
import platform.UIKit.UITextFieldTextDidEndEditingNotification
import platform.UIKit.reloadInputViews

var createLanguageTextField: ((String) -> UITextField)? = null

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun LanguageOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    languageCode: String,
    modifier: Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    var isFocused by remember { mutableStateOf(false) }

    val textField = remember {
        val factory = createLanguageTextField
        val tf = factory?.invoke(languageCode) ?: UITextField()
        tf.apply {
            borderStyle = UITextBorderStyle.UITextBorderStyleNone
            font = UIFont.systemFontOfSize(16.0)
        }
    }

    DisposableEffect(textField) {
        val changeObserver = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UITextFieldTextDidChangeNotification,
            `object` = textField,
            queue = null,
            usingBlock = { notification ->
                val tf = notification?.`object` as? UITextField
                if (tf != null) {
                    onValueChange(tf.text ?: "")
                }
            }
        )
        val beginObserver = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UITextFieldTextDidBeginEditingNotification,
            `object` = textField,
            queue = null,
            usingBlock = { _ -> isFocused = true }
        )
        val endObserver = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UITextFieldTextDidEndEditingNotification,
            `object` = textField,
            queue = null,
            usingBlock = { _ -> isFocused = false }
        )
        onDispose {
            NSNotificationCenter.defaultCenter.removeObserver(changeObserver)
            NSNotificationCenter.defaultCenter.removeObserver(beginObserver)
            NSNotificationCenter.defaultCenter.removeObserver(endObserver)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(colorScheme.background)
            .border(
                width = 1.dp,
                color = if (isFocused) colorScheme.primary else colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        UIKitView(
            factory = { textField },
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            update = { tf ->
                tf.backgroundColor = colorScheme.background.toUIColor()
                tf.borderStyle = UITextBorderStyle.UITextBorderStyleNone

                var parent = tf.superview
                while (parent != null) {
                    parent.backgroundColor = UIColor.clearColor
                    parent = parent.superview
                }

                tf.textColor = colorScheme.onBackground.toUIColor()
                tf.tintColor = colorScheme.primary.toUIColor()
                tf.placeholder = label

                try {
                    tf.setValue(value = languageCode, forKey = "languageCode")
                    if (tf.isFirstResponder()) {
                        tf.reloadInputViews()
                    }
                } catch (_: Exception) {
                }

                if (tf.text != value) {
                    tf.text = value
                }
            }
        )
    }
}

private fun Color.toUIColor(): UIColor {
    return UIColor.colorWithRed(
        red = red.toDouble(),
        green = green.toDouble(),
        blue = blue.toDouble(),
        alpha = alpha.toDouble()
    )
}
