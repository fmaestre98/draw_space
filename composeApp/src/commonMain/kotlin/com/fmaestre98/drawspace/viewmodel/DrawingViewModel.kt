package com.fmaestre98.drawspace.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.ViewModel
import com.fmaestre98.drawspace.state.DrawingAction
import com.fmaestre98.drawspace.state.DrawingState
import com.fmaestre98.drawspace.state.PathData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin

class DrawingViewModel(
) : ViewModel() {
    private val _state = MutableStateFlow(DrawingState())
    val state = _state.asStateFlow()

    fun onAction(action: DrawingAction) {
        when (action) {
            DrawingAction.OnClearCanvasClick -> onClearCanvasClick()
            is DrawingAction.OnDraw -> onDraw(action.offset)
            DrawingAction.OnNewPathStart -> onNewPathStart()
            DrawingAction.OnPathEnd -> onPathEnd()
            is DrawingAction.OnSelectColor -> onSelectColor(action.color)
            is DrawingAction.OnTap -> onTap(action.offset)
            is DrawingAction.OnShareCanvas -> shareCanvas(action.onShare)
        }
    }


    private fun onSelectColor(color: Color) {
        _state.update { it.copy(selectedColor = color) }
    }

    private fun onPathEnd() {
        val currentPathData = state.value.currentPath ?: return
        _state.update {
            it.copy(
                currentPath = null,
                paths = it.paths + currentPathData
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun onNewPathStart() {
        _state.update {
            it.copy(
                currentPath = PathData(
                    id = Clock.System.now().toEpochMilliseconds().toString(),
                    color = it.selectedColor,
                    path = emptyList()
                )
            )
        }
    }

    private fun onDraw(offset: Offset) {
        val currentPathData = state.value.currentPath ?: return
        _state.update {
            it.copy(
                currentPath = currentPathData.copy(
                    path = currentPathData.path + offset
                )
            )
        }
    }

    private fun onClearCanvasClick() {
        _state.update { it.copy(currentPath = null, paths = emptyList()) }
    }

    @OptIn(ExperimentalTime::class)
    private fun onTap(offset: Offset) {
        val pointPath = PathData(
            id = Clock.System.now().toEpochMilliseconds().toString(),
            color = state.value.selectedColor,
            path = listOf(offset)
        )
        _state.update { it.copy(paths = it.paths + pointPath) }
    }


    fun shareCanvas(onShare: ((ImageBitmap) -> Unit)) {
        val state = _state.value
        val bitmap = ImageBitmap(1080, 1920) // Adjust size as needed
        val canvas = Canvas(bitmap)

        // Draw all paths onto the bitmap
        state.paths.fastForEach { pathData ->
            val paint = Paint()
            paint.color = pathData.color
            paint.strokeWidth = 12f
            paint.strokeJoin = StrokeJoin.Round
            paint.strokeCap = StrokeCap.Round
            canvas.drawPath(
                path = Path().apply {
                    pathData.path.forEachIndexed { index, offset ->
                        if (index == 0) moveTo(offset.x, offset.y)
                        else lineTo(offset.x, offset.y)
                    }
                },
                paint
            )
        }

        // Pass the bitmap to the share callback
        onShare(bitmap)
    }
}