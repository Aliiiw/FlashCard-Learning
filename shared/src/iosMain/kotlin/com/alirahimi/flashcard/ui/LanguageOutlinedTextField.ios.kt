package com.alirahimi.flashcard.ui

import com.alirahimi.flashcard.core.designsystem.component.createLanguageTextField as designSystemTextFieldFactory
import platform.UIKit.UITextField

var createLanguageTextField: ((String) -> UITextField)?
    get() = designSystemTextFieldFactory
    set(value) {
        designSystemTextFieldFactory = value
    }
