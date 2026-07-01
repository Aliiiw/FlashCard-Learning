import UIKit
import Shared

class LanguageTextField: UITextField {
    @objc var languageCode: String = "en"

    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setup()
    }

    private func setup() {
        self.backgroundColor = .clear
        self.borderStyle = .none
        self.font = UIFont.systemFont(ofSize: 16)
    }

    override var textInputMode: UITextInputMode? {
        let activeModes = UITextInputMode.activeInputModes
        for mode in activeModes {
            if let lang = mode.primaryLanguage, lang.hasPrefix(languageCode) {
                return mode
            }
        }
        return super.textInputMode
    }
}

public func setupLanguageTextFieldFactory() {
    LanguageOutlinedTextField_iosKt.createLanguageTextField = { languageCode in
        let tf = LanguageTextField()
        tf.languageCode = languageCode
        return tf
    }
}
