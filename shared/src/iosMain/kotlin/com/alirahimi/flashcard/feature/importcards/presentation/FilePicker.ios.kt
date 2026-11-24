@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package com.alirahimi.flashcard.feature.importcards.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerMode
import platform.UIKit.UIDocumentPickerViewController
import platform.darwin.NSObject

private class DocumentPickerDelegate(
    private val onFilePicked: (String) -> Unit
) : NSObject(), UIDocumentPickerDelegateProtocol {
    override fun documentPicker(
        controller: UIDocumentPickerViewController,
        didPickDocumentsAtURLs: List<*>
    ) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL ?: return
        val isAccessed = url.startAccessingSecurityScopedResource()
        try {
            val content = NSString.stringWithContentsOfURL(
                url = url,
                encoding = NSUTF8StringEncoding,
                error = null
            )
            if (content != null) {
                onFilePicked(content)
            }
        } finally {
            if (isAccessed) {
                url.stopAccessingSecurityScopedResource()
            }
        }
    }
}

@Composable
actual fun rememberFilePicker(onFileContentRead: (String) -> Unit): () -> Unit {
    val delegate = remember {
        DocumentPickerDelegate { content ->
            onFileContentRead(content)
        }
    }

    return {
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        if (rootViewController != null) {
            val documentPicker = UIDocumentPickerViewController(
                documentTypes = listOf("public.text"),
                inMode = UIDocumentPickerMode.UIDocumentPickerModeImport
            )
            documentPicker.delegate = delegate
            rootViewController.presentViewController(
                viewControllerToPresent = documentPicker,
                animated = true,
                completion = null
            )
        }
    }
}
