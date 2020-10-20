package com.axionesl.medcheck.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.axionesl.medcheck.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PatientAppointmentActivity : AppCompatActivity() {
    private lateinit var addAppointment: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_appointment)
        bindWidgets()
        bindListeners()
    }

    private fun bindWidgets() {
        addAppointment = findViewById(R.id.add_appointment)
    }

    private fun bindListeners() {
        addAppointment.setOnClickListener {
            startActivity(Intent(this, AddAppointmentActivity::class.java))
        }
    }
}