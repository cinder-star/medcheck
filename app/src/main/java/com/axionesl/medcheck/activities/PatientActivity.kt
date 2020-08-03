package com.axionesl.medcheck.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.axionesl.medcheck.R

class PatientActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient)
        title = "Tests"
        bindWidgets()
        bindListeners()
    }

    private fun bindWidgets() {
        TODO("Not yet implemented")
    }

    private fun bindListeners() {
        TODO("Not yet implemented")
    }
}