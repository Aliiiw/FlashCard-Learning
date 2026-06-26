import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        IosKoinKt.initKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}