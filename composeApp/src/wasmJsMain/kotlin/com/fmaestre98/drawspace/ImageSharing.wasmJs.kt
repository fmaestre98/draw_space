package com.fmaestre98.drawspace

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.browser.document
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLCanvasElement


actual fun shareImage(image: ImageBitmap) {
    val canvas = document.createElement("canvas") as HTMLCanvasElement
    canvas.width = image.width
    canvas.height = image.height
    val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
    ctx.drawImage(image as org.w3c.dom.ImageBitmap, 0.0, 0.0)
    val dataUrl = canvas.toDataURL("image/png")
    val anchor = document.createElement("a") as HTMLAnchorElement
    anchor.href = dataUrl
    anchor.download = "shared_image.png"
    anchor.style.display = "none"
    document.body?.appendChild(anchor)
    anchor.click()
    document.body?.removeChild(anchor)
}