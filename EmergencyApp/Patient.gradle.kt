package com.example.emergencywatch

data class Patient(
    val id: String,
    val name: String,
    val age: Int,
    val bloodType: String,
    val knownDiseases: List<String>,
    val suspectedEmergency: String,
    val medications: List<String>,
    val heartRateHistory: List<Int> // Für den Graphen
)

// Mock Data Provider (Simuliert die Datenbank/Backend)
object PatientRepository {
    fun getMockPatient(): Patient {
        return Patient(
            id = "1",
            name = "Max Mustermann",
            age = 65,
            bloodType = "AB-",
            knownDiseases = listOf("Hypertonie", "Diabetes Typ 2"),
            suspectedEmergency = "Herzrhythmusstörung",
            medications = listOf("Metoprolol", "Aspirin", "Insulin"),
            heartRateHistory = listOf(70, 72, 110, 130, 90, 85, 180, 160, 110)
        )
    }
}