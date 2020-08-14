package com.axionesl.medcheck.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.axionesl.medcheck.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddTestActivity : AppCompatActivity() {
    private lateinit var heightCm: TextInputEditText
    private lateinit var heightCmHolder: TextInputLayout
    private lateinit var heightFeet: LinearLayout
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
    }
}