package com.juanalejop.movil

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform