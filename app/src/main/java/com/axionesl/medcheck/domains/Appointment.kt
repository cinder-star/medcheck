package com.axionesl.medcheck.domains

data class Appointment(
    val doctorName: String? = null,
    val patientName: String? = null,
    val patientNumber: String? = null,
    val date: String? = null,
    val time: String? = null,
    val status: String? = "Not Reviewed"
)