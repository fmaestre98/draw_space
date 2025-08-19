package com.fmaestre98.drawspace.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach

@Composable
fun ColumnScope.CanvasControls(
    selectedColor: Color,
    colors: List<Color>,
    onSelectColor: (Color) -> Unit,
    onClearCanvas: () -> Unit,
    onShareCanvas: () -> Unit,
    onToggleTheme: () -> Unit, // Add this parameter
    isDarkTheme: Boolean, // Add this parameter
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(visible = isVisible) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Color Picker Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                colors.fastForEach { color ->
                    val isSelected = selectedColor == color
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                val scale = if (isSelected) 1.2f else 1f
                                scaleX = scale
                                scaleY = scale
                            }
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = 2.dp,
                                color = if (isSelected) Color.Black else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { onSelectColor(color) }
                    )
                }
            }

            // Action Buttons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                IconButton(onClick = onClearCanvas) {
                    Icon(
                        imageVector = Icons.Default.CleaningServices,
                        contentDescription = "Clear Canvas"
                    )
                }
                IconButton(onClick = onShareCanvas) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Canvas"
                    )
                }
                // Add theme toggle button
                IconButton(onClick = onToggleTheme) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (isDarkTheme) "Switch to Light Theme" else "Switch to Dark Theme"
                    )
                }
            }
        }
    }
}