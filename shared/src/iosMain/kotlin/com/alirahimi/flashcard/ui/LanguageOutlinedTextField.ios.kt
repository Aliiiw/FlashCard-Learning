package com.alirahimi.flashcard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import platform.UIKit.*
import platform.Foundation.*
import kotlinx.cinterop.ExperimentalForeignApi

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
    val colors = LocalAppColors.current
    var isFocused by remember { mutableStateOf(false) }

    val textField = remember {
        val factory = createLanguageTextField
        val tf = if (factory != null) {
            factory(languageCode)
        } else {
            UITextField()
        }
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
            .background(colors.background)
            .border(
                width = 1.dp,
                color = if (isFocused) colors.primary else colors.borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        UIKitView(
            factory = { textField },
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            background = Color.Transparent,
            update = { tf ->
                tf.backgroundColor = colors.background.toUIColor()
                tf.borderStyle = UITextBorderStyle.UITextBorderStyleNone
                
                var parent = tf.superview
                while (parent != null) {
                    parent.backgroundColor = UIColor.clearColor
                    parent = parent.superview
                }

                tf.textColor = colors.textPrimary.toUIColor()
                tf.tintColor = colors.primary.toUIColor()
                tf.placeholder = label
                
                try {
                    tf.setValue(value = languageCode, forKey = "languageCode")
                    if (tf.isFirstResponder()) {
                        tf.reloadInputViews()
                    }
                } catch (e: Exception) {
                }

                if (tf.text != value) {
                    tf.text = value
                }
            }
        )
    }
}

@Composable
actual fun Modifier.clearFocusOnTap(): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures(onTap = {
            UIApplication.sharedApplication.keyWindow?.endEditing(true)
        })
    }
}

private fun Color.toUIColor(): UIColor {
    return UIColor.colorWithRed(
        red = this.red.toDouble(),
        green = this.green.toDouble(),
        blue = this.blue.toDouble(),
        alpha = this.alpha.toDouble()
    )
}
