package com.example.dailydex

interface Platform {
    val name: String
}
expect fun getPlatform(): Platform
