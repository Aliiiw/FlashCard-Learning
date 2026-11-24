package com.alirahimi.flashcard

import android.app.Application
import com.alirahimi.flashcard.di.initKoinAndroid

class FlashCardApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoinAndroid(this)
    }
}
