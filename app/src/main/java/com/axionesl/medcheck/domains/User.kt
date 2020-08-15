package com.axionesl.medcheck.domains

data class User(
    val id: String? = null,
    val email: String? = null,
    var fullName: String? = null,
    var mobileNumber: String? = null,
    val accountType: String? = null
)