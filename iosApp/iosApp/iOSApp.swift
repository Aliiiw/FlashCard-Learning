import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        setupLanguageTextFieldFactory()
        IosKoin.shared.doInitKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}