package com.fmaestre98.drawspace

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform