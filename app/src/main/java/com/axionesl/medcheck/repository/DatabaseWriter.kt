package com.axionesl.medcheck.repository

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DatabaseWriter {
    companion object {
        fun write(path: String, obj: Any?) {
            val database = Firebase.database.reference
            database.child(path).setValue(obj)
        }
    }
}