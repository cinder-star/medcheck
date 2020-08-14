package com.axionesl.medcheck.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.Test
import com.axionesl.medcheck.repository.DatabaseWriter
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AddTestActivity : AppCompatActivity() {
    private lateinit var heightCm: TextInputEditText
    private lateinit var heightCmHolder: TextInputLayout
    private lateinit var heightFeet: LinearLayout
    private lateinit var heightFtValue: TextInputEditText
    private lateinit var heightInchValue: TextInputEditText
    private lateinit var weight: TextInputEditText
    private lateinit var bpm: TextInputEditText
    private lateinit var glucoseLevel: TextInputEditText
    private lateinit var oxygenLevel: TextInputEditText
    private lateinit var bloodPressure: TextInputEditText
    private lateinit var temperature: TextInputEditText
    private lateinit var problemDetails: TextInputEditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var createTest: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_test)
        bindWidgets()
        bindListeners()
    }
    
    private fun bindWidgets() {
        heightCm = findViewById(R.id.height_cm)
        heightCmHolder = findViewById(R.id.height_cm_holder)
        heightFeet = findViewById(R.id.height_ft_inch)
        radioGroup = findViewById(R.id.height)
        createTest = findViewById(R.id.create_test)
        weight = findViewById(R.id.weight)
        bpm = findViewById(R.id.bpm)
        glucoseLevel = findViewById(R.id.glucose_level)
        oxygenLevel = findViewById(R.id.oxygen_level)
        bloodPressure = findViewById(R.id.blood_pressure)
        temperature = findViewById(R.id.temperature)
        problemDetails = findViewById(R.id.problem_details)
        heightFtValue = findViewById(R.id.height_feet)
        heightInchValue = findViewById(R.id.height_inch)
    }
    
    private fun bindListeners() {
        radioGroup.setOnCheckedChangeListener { _, i ->
            val radioButton: RadioButton = findViewById(i)
            val text = radioButton.text.toString()
            if (text == "Cm") {
                heightFeet.visibility = View.GONE
                heightCm.visibility = View.VISIBLE
                heightCmHolder.visibility = View.VISIBLE
            } else {
                heightFeet.visibility = View.VISIBLE
                heightCm.visibility = View.GONE
                heightCmHolder.visibility = View.GONE
            }
        }
        createTest.setOnClickListener {
            if (validate()) {
                val test: Test = getTestData()
                DatabaseWriter.write("/tests/"+test.id, test)
                finish()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTestData(): Test {
        @Suppress("SpellCheckingInspection")
        val id = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val date = SimpleDateFormat("dd-MM-yyyy").format(Date())
        val patient: String? = Firebase.auth.currentUser!!.uid
        var details: String? = null
        if (problemDetails.text.toString().isNotEmpty()) {
            details = problemDetails.text.toString()
        }
        return Test(
            id,
            weight.text.toString().toDouble(),
            getHeight(),
            getBmi(),
            bpm.text.toString().toDouble(),
            bloodPressure.text.toString().toDouble(),
            glucoseLevel.text.toString().toDouble(),
            oxygenLevel.text.toString().toDouble(),
            temperature.text.toString().toDouble(),
            details,
            "In Queue",
            null,
            patient,
            date
        )
    }

    private fun getBmi(): Double {
        var height: Double
        val buttonId = radioGroup.checkedRadioButtonId
        val radioButton: RadioButton = findViewById(buttonId)
        val text = radioButton.text.toString()
        if (text == "Cm") {
            height = heightCm.text.toString().toDouble()*0.01
        } else {
            height = heightFtValue.text.toString().toDouble()*12.0+heightInchValue.text.toString().toDouble()
            height *= 0.0254
        }
        return (weight.text.toString().toDouble())/height
    }

    private fun validate(): Boolean {
        var value = true
        val texts: ArrayList<TextInputEditText> = arrayListOf(weight, bpm, bloodPressure, glucoseLevel, oxygenLevel, temperature)
        texts.forEach {
            if (it.text?.isEmpty()!!){
                it.error = "Cannot be empty!"
                it.requestFocus()
                value = false
            }
        }
        val buttonId = radioGroup.checkedRadioButtonId
        val radioButton: RadioButton = findViewById(buttonId)
        val text = radioButton.text.toString()
        if (text == "Cm") {
            if (heightCm.text!!.isEmpty()){
                heightCm.error = "Cannot be empty!"
                heightCm.requestFocus()
                value = false
            }
        } else {
            if (heightFtValue.text!!.isEmpty()){
                heightFtValue.error = "Cannot be empty!"
                heightFtValue.requestFocus()
                value = false
            }
            if (heightInchValue.text!!.isEmpty()){
                heightInchValue.error = "Cannot be empty!"
                heightInchValue.requestFocus()
                value = false
            }
        }
        return value
    }

    private fun getHeight(): String {
        val buttonId = radioGroup.checkedRadioButtonId
        val radioButton: RadioButton = findViewById(buttonId)
        val text = radioButton.text.toString()
        return if (text == "Cm") {
            heightCm.text.toString() + "cm"
        } else {
            heightFtValue.text.toString()+"ft. "+ heightInchValue.text.toString()+"in."
        }
    }
}