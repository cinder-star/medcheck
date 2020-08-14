package com.axionesl.medcheck.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.axionesl.medcheck.R

class TestDetailsActivity : AppCompatActivity() {
    private lateinit var testId: TextView
    private lateinit var testPatientName: TextView
    private lateinit var testHeight: TextView
    private lateinit var testWeight: TextView
    private lateinit var testBMI: TextView
    private lateinit var testBPM: TextView
    private lateinit var testGlucoseLevel: TextView
    private lateinit var testOxygenLevel: TextView
    private lateinit var testTemperature: TextView
    private lateinit var testProblemDetails: TextView
    private lateinit var testStatus: TextView
    private lateinit var testCheckedBy: TextView
    private lateinit var testDate: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_details)
        bindWidgets()
    }

    private fun bindWidgets() {
        testId = findViewById(R.id.test_id)
        testPatientName = findViewById(R.id.test_patient_name)
        testHeight = findViewById(R.id.test_height)
        testWeight = findViewById(R.id.test_weight)
        testBMI = findViewById(R.id.test_bmi)
        testBPM = findViewById(R.id.test_bpm)
        testGlucoseLevel = findViewById(R.id.test_glucose_level)
        testOxygenLevel = findViewById(R.id.test_oxygen_level)
        testTemperature = findViewById(R.id.test_temperature)
        testProblemDetails = findViewById(R.id.test_problem_details)
        testStatus = findViewById(R.id.test_status)
        testCheckedBy = findViewById(R.id.test_checked_by)
        testDate = findViewById(R.id.test_date)
    }
}