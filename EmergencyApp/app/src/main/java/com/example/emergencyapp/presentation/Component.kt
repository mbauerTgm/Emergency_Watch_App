package com.example.emergencyapp.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text

@Composable
fun HeartRateGraph(dataPoints: List<Int>, modifier: Modifier = Modifier) {
    if (dataPoints.isEmpty()) return

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val maxVal = 190f // Skala aus Skizze 2
            val minVal = 50f
            val width = size.width
            val height = size.height

            val path = Path()
            val stepX = width / (dataPoints.size - 1)

            dataPoints.forEachIndexed { index, heartRate ->
                val x = index * stepX
                // Y-Achse invertieren (0 ist oben), normalisieren auf Box-Höhe
                val normalizedY = 1 - ((heartRate - minVal) / (maxVal - minVal))
                val y = normalizedY * height

                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }

            drawPath(
                path = path,
                color = Color.Red,
                style = Stroke(width = 3.dp.toPx())
            )
        }
    }
}