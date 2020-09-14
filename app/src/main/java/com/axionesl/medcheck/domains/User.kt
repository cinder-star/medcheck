package com.axionesl.medcheck.domains

data class User(
    val id: String? = null,
    val email: String? = null,
    var fullName: String? = null,
    var mobileNumber: String? = null,
    val accountType: String? = null,
    var bloodType: String? = null,
    var dateOfBirth: String? = null,
    var profilePicturePath: String? = null,
    var lastUpdated: String? = null,
    var doctorType: String? = null,
    var degree: String? = null,
    var currentDesignation: String? = null
)