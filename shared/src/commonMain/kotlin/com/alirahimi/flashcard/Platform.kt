package com.alirahimi.flashcard

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform