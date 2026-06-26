package com.alirahimi.flashcard.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext

fun initKoinAndroid(context: Context) {
    if (GlobalContext.getOrNull() == null) {
        initKoin {
            androidContext(context)
        }
    }
}
