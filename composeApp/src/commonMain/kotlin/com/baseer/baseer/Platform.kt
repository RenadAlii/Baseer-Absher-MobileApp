package com.baseer.baseer

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform