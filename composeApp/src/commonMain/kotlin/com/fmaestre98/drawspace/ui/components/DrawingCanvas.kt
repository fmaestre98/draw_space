package com.fmaestre98.drawspace.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.util.fastForEach
import com.fmaestre98.drawspace.ui.state.DrawingAction
import com.fmaestre98.drawspace.ui.state.PathData
import kotlin.math.abs

@Composable
fun DrawingCanvas(
    paths: List<PathData>,
    currentPath: PathData?,
    onAction: (DrawingAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        onAction(DrawingAction.OnTap(offset))
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        onAction(DrawingAction.OnNewPathStart)
                    },
                    onDragEnd = {
                        onAction(DrawingAction.OnPathEnd)
                    },
                    onDrag = { change, _ ->
                        onAction(DrawingAction.OnDraw(change.position))
                    },
                    onDragCancel = {
                        onAction(DrawingAction.OnPathEnd)
                    }
                )
            }
    ) {
        paths.fastForEach { pathData ->
            drawPath(
                path = pathData.path,
                color = pathData.color,
            )
        }
        currentPath?.let {
            drawPath(
                path = it.path,
                color = it.color
            )
        }
    }
}

private fun DrawScope.drawPath(
    path: List<Offset>,
    color: Color,
    thickness: Float = 12f
) {
    if (path.size == 1) {
        drawCircle(
            color = color,
            radius = thickness / 2,
            center = path.first()
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
        drawPath(
            path = smoothedPath,
            color = color,
            style = Stroke(
                width = thickness,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}