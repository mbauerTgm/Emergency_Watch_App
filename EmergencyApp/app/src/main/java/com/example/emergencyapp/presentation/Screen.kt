package com.example.emergencywatch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*

// ----------------------------------------------------------------
// Hilfskomponente: Der Herzfrequenz-Graph
// ----------------------------------------------------------------
@Composable
fun HeartRateGraph(dataPoints: List<Int>, modifier: Modifier = Modifier) {
    if (dataPoints.isEmpty()) return

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val maxVal = 190f
            val minVal = 50f
            val width = size.width
            val height = size.height

            val path = Path()
            // Sicherheitscheck, um Division durch 0 zu vermeiden
            val stepX = if (dataPoints.size > 1) width / (dataPoints.size - 1) else 0f

            dataPoints.forEachIndexed { index, heartRate ->
                val x = index * stepX
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

// ----------------------------------------------------------------
// Screen 1: Simulierter Notruf-Eingang
// ----------------------------------------------------------------
@Composable
fun IncomingEmergencyScreen(onAccept: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "⚠️ NOTFALL",
            color = Color.Red,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Patient in Nähe", color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))

        // Der Button wurde angepasst
        Button(
            onClick = onAccept,
            colors = ButtonDefaults.primaryButtonColors(backgroundColor = Color.Red),
            // FIX: Wir zwingen den Button auf 70% Breite, damit der Text passt
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text(
                "Anzeigen",
                maxLines = 1,       // Erzwingt eine Zeile
                softWrap = false    // Verbietet Umbruch, selbst wenn es eng wird
            )
        }
    }
}

// ----------------------------------------------------------------
// Screen 2: Dashboard (Mit deinen Layout-Fixes)
// ----------------------------------------------------------------
@Composable
fun PatientDashboardScreen(patient: Patient, onNavigateToMenu: () -> Unit) {
    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        autoCentering = AutoCenteringParams(itemIndex = 0)
    ) {
        // --- Header ---
        item {
            Text(
                text = "Dr. Connected",
                color = Color.Cyan,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }

        // --- Name (FIX: Kleiner & Zentriert) ---
        item {
            Text(
                text = patient.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }

        // --- Known Diseases ---
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Known Diseases:", color = Color.Gray, fontSize = 10.sp)
                Text(
                    patient.knownDiseases.joinToString(", "),
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        // --- Suspected Emergency (FIX: Gruppiert gegen Überlappung) ---
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Suspected Emergency:", color = Color.Gray, fontSize = 10.sp)

                // Wichtiger Abstand gegen Überlappung:
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = patient.suspectedEmergency,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        // --- Blutwerte & Graph ---
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("Blut: ${patient.bloodType}", color = Color.White, fontSize = 12.sp)
                    Text("Alter: ${patient.age}", color = Color.White, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))

                // Mini-Graph Vorschau
                HeartRateGraph(
                    dataPoints = patient.heartRateHistory.takeLast(5),
                    modifier = Modifier.size(width = 50.dp, height = 30.dp)
                )
            }
        }

        // --- Button (FIX: Einzeilig) ---
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Chip(
                onClick = onNavigateToMenu,
                label = {
                    Text(
                        "Details & Aktionen",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,    // Verbietet mehr als 1 Zeile
                        softWrap = false // Verbietet Umbruch
                    )
                },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            // Extra Platz zum Scrollen
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// ----------------------------------------------------------------
// Screen 3: Menü
// ----------------------------------------------------------------
@Composable
fun PatientMenuScreen(patient: Patient, onShowVitals: () -> Unit) {
    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        autoCentering = AutoCenteringParams(itemIndex = 0)
    ) {
        item {
            ListHeader {
                Text("Patienten Daten", color = Color.White)
            }
        }

        item {
            Chip(
                onClick = { /* TODO: Show Details */ },
                label = { Text("Persönliche Daten") },
                secondaryLabel = { Text(patient.name) },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth(0.9f)
            )
        }
        item {
            Chip(
                onClick = { /* TODO: Show Meds */ },
                label = { Text("Medikamente") },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth(0.9f)
            )
        }
        item {
            Chip(
                onClick = onShowVitals,
                label = { Text("Aktive Messungen") },
                colors = ChipDefaults.primaryChipColors(),
                modifier = Modifier.fillMaxWidth(0.9f)
            )
        }
    }
}

// ----------------------------------------------------------------
// Screen 4: Großer Graph (Details)
// ----------------------------------------------------------------
@Composable
fun VitalSignsScreen(patient: Patient) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Herzfrequenz", fontSize = 14.sp, color = Color.Gray)

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(100.dp)
                .background(Color.DarkGray.copy(alpha = 0.2f))
                .padding(4.dp)
        ) {
            // Y-Achsen Labels
            Text(
                "190",
                fontSize = 10.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.TopStart)
            )
            Text(
                "70",
                fontSize = 10.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.BottomStart)
            )

            // Der große Graph
            HeartRateGraph(
                dataPoints = patient.heartRateHistory,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Hzf: ${patient.heartRateHistory.last()} bpm",
            fontSize = 16.sp,
            color = Color.White
        )
        Text(
            "Highest: ${patient.heartRateHistory.maxOrNull()} bpm",
            fontSize = 12.sp,
            color = Color.LightGray
        )
    }
}