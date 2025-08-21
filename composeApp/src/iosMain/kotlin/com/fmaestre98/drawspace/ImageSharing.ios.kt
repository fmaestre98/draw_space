package com.fmaestre98.drawspace

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

actual fun shareImage(image: ImageBitmap) {
    val skiaImage = Image.makeFromBitmap(image.asSkiaBitmap())
    val data = skiaImage.encodeToData(EncodedImageFormat.PNG) ?: error("Encoding failed")
    val bytes = data.bytes
    val tempDir = NSTemporaryDirectory()
    val tempFilePath = tempDir + "shared_image.png"
    @OptIn(ExperimentalForeignApi::class)
    val nsData = bytes.usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
    }
    nsData.writeToFile(tempFilePath, atomically = true)
    val url = NSURL.fileURLWithPath(tempFilePath)
    val activityViewController = UIActivityViewController(listOf(url), null)
    UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
        activityViewController, animated = true, completion = null
    )
}