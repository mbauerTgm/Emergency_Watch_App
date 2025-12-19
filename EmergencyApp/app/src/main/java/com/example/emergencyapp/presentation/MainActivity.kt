package com.example.emergencyapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.emergencywatch.IncomingEmergencyScreen
import com.example.emergencywatch.PatientDashboardScreen
import com.example.emergencywatch.PatientMenuScreen
import com.example.emergencywatch.PatientRepository
import com.example.emergencywatch.VitalSignsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mock Patient laden
        val currentPatient = PatientRepository.getMockPatient()

        setContent {
            val navController = rememberSwipeDismissableNavController()

            // Navigation Graph
            SwipeDismissableNavHost(
                navController = navController,
                startDestination = "incoming_emergency"
            ) {

                // 1. Notfall Benachrichtigung
                composable("incoming_emergency") {
                    IncomingEmergencyScreen(
                        onAccept = { navController.navigate("dashboard") }
                    )
                }

                // 2. Übersicht (Runde Skizze)
                composable("dashboard") {
                    PatientDashboardScreen(
                        patient = currentPatient,
                        onNavigateToMenu = { navController.navigate("menu") }
                    )
                }

                // 3. Menü Auswahl
                composable("menu") {
                    PatientMenuScreen(
                        patient = currentPatient,
                        onShowVitals = { navController.navigate("vitals") }
                    )
                }

                // 4. Detail Graph
                composable("vitals") {
                    VitalSignsScreen(patient = currentPatient)
                }
            }
        }
    }
}