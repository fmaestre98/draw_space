package com.fmaestre98.drawspace.ui.state

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class DrawingState(
    val selectedColor: Color = Color.Black,
    val currentPath: PathData? = null,
    val paths: List<PathData> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class PathData(
    val id: String,
    val color: Color,
    val path: List<Offset>
)

val allColors = listOf(
    Color.Black,
    Color.Red,
    Color.Blue,
    Color.Green,
    Color.Yellow,
    Color.Magenta,
    Color.Cyan,
)