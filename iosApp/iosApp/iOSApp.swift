import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        IosKoin.shared.doInitKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}