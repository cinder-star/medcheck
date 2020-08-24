package com.axionesl.medcheck.fragments

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.Test
import com.axionesl.medcheck.domains.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import io.paperdb.Paper

abstract class AbstractTestDetailsActivity(private val xmlId: Int) : AppCompatActivity() {
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
        setContentView(xmlId)
        bindWidgets()
    }

    override fun onStart() {
        super.onStart()
        val reference = Firebase
            .database
            .reference
            .child("/tests/")
            .orderByChild("id")
            .equalTo(Paper.book().read<Test>("test", null).id)
        reference.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val test: Test? = it.getValue<Test>()
                    updateUI(test)
                }
            }

            private fun updateUI(test: Test?) {
                runOnUiThread {
                    testId.text = test!!.id
                    testPatientName.text = Paper.book().read<User>("user", null).fullName
                    testHeight.text = test.height
                    testWeight.text = test.weight.toString()
                    testBMI.text = test.bmi.toString()
                    testBPM.text = test.bpm.toString()
                    testGlucoseLevel.text = test.glucoseLevel.toString()
                    testOxygenLevel.text = test.oxygenLevel.toString()
                    testTemperature.text = test.temperature.toString()
                    testProblemDetails.text = test.problemDetails
                    val status = test.status
                    if (status == "In Queue"){
                        testStatus.setTextColor(Color.parseColor("#FF0000"))
                    } else {
                        testStatus.setTextColor(Color.parseColor("#008000"))
                    }
                    testStatus.text = test.status
                    if (test.checkedBy != null) {
                        testCheckedBy.text = test.checkedBy
                    }
                    testDate.text = test.date
                }
            }
        })
    }

    open fun bindWidgets() {
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

    abstract fun bindListeners()
}