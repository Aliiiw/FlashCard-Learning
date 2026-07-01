@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package com.alirahimi.flashcard.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.*
import platform.Foundation.*
import platform.darwin.NSObject

class DocumentPickerDelegate(
    private val onFilePicked: (String) -> Unit
) : NSObject(), UIDocumentPickerDelegateProtocol {
    override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL ?: return
        val isAccessed = url.startAccessingSecurityScopedResource()
        try {
            val content = NSString.stringWithContentsOfURL(url, encoding = NSUTF8StringEncoding, error = null)
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
            rootViewController.presentViewController(documentPicker, animated = true, completion = null)
        }
    }
}
