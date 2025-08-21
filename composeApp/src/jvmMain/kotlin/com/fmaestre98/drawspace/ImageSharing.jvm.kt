package com.fmaestre98.drawspace

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

actual fun shareImage(image: ImageBitmap) {
    val chooser = JFileChooser().apply {
        dialogTitle = "Save Image"
        fileFilter = FileNameExtensionFilter("PNG Images", "png")
        selectedFile = File("shared_image.png")
    }
    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
        var file = chooser.selectedFile
        if (!file.name.endsWith(".png")) {
            file = File(file.path + ".png")
        }
        val awtImage = image.toAwtImage()
        ImageIO.write(awtImage, "png", file)
    }
}
