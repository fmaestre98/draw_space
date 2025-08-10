package com.fmaestre98.drawspace

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fmaestre98.drawspace.ui.components.CanvasControls
import com.fmaestre98.drawspace.ui.components.DrawingCanvas
import com.fmaestre98.drawspace.ui.state.DrawingAction
import com.fmaestre98.drawspace.ui.state.allColors
import com.fmaestre98.drawspace.ui.theme.DrawSpaceAppTheme
import com.fmaestre98.drawspace.ui.viewmodel.DrawingViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import drawspace.composeapp.generated.resources.Res
import drawspace.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App(viewModel: DrawingViewModel = viewModel { DrawingViewModel() }) {
    DrawSpaceAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            val state by viewModel.state.collectAsStateWithLifecycle()
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
                )
                CanvasControls(
                    selectedColor = state.selectedColor,
                    colors = allColors,
                    onSelectColor = {
                        viewModel.onAction(DrawingAction.OnSelectColor(it))
                    },
                    onClearCanvas = {
                        viewModel.onAction(DrawingAction.OnClearCanvasClick)
                    }
                )
            }
        }
    }
}