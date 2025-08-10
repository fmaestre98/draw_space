package com.fmaestre98.drawspace.ui.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmaestre98.drawspace.ui.state.DrawingAction
import com.fmaestre98.drawspace.ui.state.DrawingState
import com.fmaestre98.drawspace.ui.state.PathData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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
}