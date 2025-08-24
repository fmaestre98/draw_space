package com.fmaestre98.drawspace


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fmaestre98.drawspace.ui.components.CanvasControls
import com.fmaestre98.drawspace.ui.components.DrawingCanvas
import com.fmaestre98.drawspace.state.DrawingAction
import com.fmaestre98.drawspace.state.allColors
import com.fmaestre98.drawspace.ui.theme.DrawSpaceAppTheme
import com.fmaestre98.drawspace.viewmodel.DrawingViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(viewModel: DrawingViewModel = viewModel { DrawingViewModel() }) {
    val theme = isSystemInDarkTheme()
    var isDarkTheme by remember { mutableStateOf(theme) }

    DrawSpaceAppTheme(darkTheme = isDarkTheme) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            val state by viewModel.state.collectAsStateWithLifecycle()
            var isControlsVisible by remember { mutableStateOf(true) }
            var canvasSize by remember { mutableStateOf(IntSize.Zero) }
            val backgroundColor = MaterialTheme.colorScheme.background

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DrawingCanvas(
                    paths = state.paths,
                    currentPath = state.currentPath,
                    onAction = viewModel::onAction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .onSizeChanged { canvasSize = it }
                )
                IconButton(onClick = { isControlsVisible = !isControlsVisible }) {
                    Icon(
                        imageVector = if (isControlsVisible) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropUp,
                        contentDescription = "Toggle Controls"
                    )
                }
                CanvasControls(
                    selectedColor = state.selectedColor,
                    colors = allColors,
                    onSelectColor = {
                        viewModel.onAction(DrawingAction.OnSelectColor(it))
                    },
                    onClearCanvas = {
                        viewModel.onAction(DrawingAction.OnClearCanvasClick)
                    },
                    onShareCanvas = {
                        if (canvasSize != IntSize.Zero) {
                            viewModel.onAction(
                                DrawingAction.OnShareCanvas(
                                    width = canvasSize.width,
                                    height = canvasSize.height,
                                    backgroundColor = backgroundColor,
                                    onShare = { imageBitmap ->
                                        shareImage(imageBitmap)
                                    }
                                )
                            )
                        }
                    },
                    onToggleTheme = { isDarkTheme = !isDarkTheme },
                    isDarkTheme = isDarkTheme,
                    isVisible = isControlsVisible
                )
            }
        }
    }
}