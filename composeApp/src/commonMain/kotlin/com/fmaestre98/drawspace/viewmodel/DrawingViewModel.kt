package com.fmaestre98.drawspace.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.ViewModel
import com.fmaestre98.drawspace.state.DrawingAction
import com.fmaestre98.drawspace.state.DrawingState
import com.fmaestre98.drawspace.state.PathData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import androidx.compose.ui.graphics.Canvas as ComposeCanvas

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
            is DrawingAction.OnShareCanvas -> shareCanvas(
                action.width,
                action.height,
                action.backgroundColor,
                action.onShare
            )
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


    private fun shareCanvas(width: Int, height: Int, backgroundColor: Color, onShare: (ImageBitmap) -> Unit) {
        val state = _state.value
        val bitmap = ImageBitmap(width, height)
        val canvas = ComposeCanvas(bitmap)

        // Draw background
        canvas.drawRect(
            Rect(
                offset = Offset.Zero,
                size = Size(width.toFloat(), height.toFloat())
            ),
            Paint().apply { color = backgroundColor }
        )

        val allPaths = state.paths + listOfNotNull(state.currentPath)
        allPaths.fastForEach { pathData ->
            drawPath(
                canvas = canvas,
                path = pathData.path,
                color = pathData.color
            )
        }

        onShare(bitmap)
    }

    private fun drawPath(
        canvas: ComposeCanvas,
        path: List<Offset>,
        color: Color,
        thickness: Float = 12f
    ) {
        if (path.size == 1) {
            canvas.drawCircle(
                center = path.first(),
                radius = thickness / 2,
                paint = Paint().apply { this.color = color }
            )
        } else if (path.isNotEmpty()) {
            val smoothedPath = Path().apply {
                moveTo(path.first().x, path.first().y)
                val smoothness = 5
                for (i in 1..path.lastIndex) {
                    val from = path[i - 1]
                    val to = path[i]
                    val dx = abs(from.x - to.x)
                    val dy = abs(from.y - to.y)
                    if (dx >= smoothness || dy >= smoothness) {
                        quadraticTo(
                            x1 = (from.x + to.x) / 2f,
                            y1 = (from.y + to.y) / 2f,
                            x2 = to.x,
                            y2 = to.y
                        )
                    }
                }
            }
            canvas.drawPath(
                path = smoothedPath,
                paint = Paint().apply {
                    this.color = color
                    strokeWidth = thickness
                    strokeCap = StrokeCap.Round
                    strokeJoin = StrokeJoin.Round
                    style = PaintingStyle.Stroke
                }
            )
        }
    }
}