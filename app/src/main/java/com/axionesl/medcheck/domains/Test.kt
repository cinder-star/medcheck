package com.axionesl.medcheck.domains

import java.io.Serializable

data class Test(
    val id: String? = null,
    val weight: Double? = 0.0,
    val height: String? = null,
    val bmi: Double? = 0.0,
    val bpm: Double? = 0.0,
    val bloodPressure: Double? = 0.0,
    val glucoseLevel: Double? = 0.0,
    val oxygenLevel: Double? = 0.0,
    val temperature: Double? = 0.0,
    val problemDetails: String? = null,
    var status: String? = "In Queue",
    var checkedBy: String? = null,
    var patient: String? = null,
    var date: String? = null,
    var prescription: String? = null,
    var mobileNumber: String? = null,
    var testPicture: String? = null,
    var patientName: String? = null
) : Serializable