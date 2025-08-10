package com.fmaestre98.drawspace

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "DrawSpace",
    ) {
        App()
    }
}