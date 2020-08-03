package com.axionesl.medcheck.domains

data class Test (
    val weight: Double? = 0.0,
    val height: Double? = 0.0,
    val bpm: Double? = 0.0,
    val bloodPressure: Double? = 0.0,
    val glucoseLevel: Double? = 0.0,
    val oxygenLevel: Double? = 0.0
)