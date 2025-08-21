// androidMain
package com.fmaestre98.drawspace

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.content.FileProvider
import java.io.File


object PlatformContext {
    var appContext: Context? = null

    fun getContext(): Context =
        appContext ?: error("Android context not set. Set Platform.appContext in your Application.onCreate()")
}

actual fun shareImage(image: ImageBitmap) {
    val context = PlatformContext.getContext()
    val bitmap = image.asAndroidBitmap()
    val cacheDir = context.cacheDir
    val file = File(cacheDir, "shared_image.png")
    file.outputStream().use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(Intent.createChooser(intent, "Share Image"))
}