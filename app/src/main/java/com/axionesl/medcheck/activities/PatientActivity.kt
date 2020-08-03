package com.axionesl.medcheck.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.axionesl.medcheck.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PatientActivity : AppCompatActivity() {
    private lateinit var addTest: FloatingActionButton
    private lateinit var testList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient)
        title = "Tests"
        bindWidgets()
        bindListeners()
    }

    private fun bindWidgets() {
        addTest = findViewById(R.id.add_test)
        testList = findViewById(R.id.test_list)
    }

    private fun bindListeners() {
        addTest.setOnClickListener {
            startActivity(Intent(this, AddTestActivity::class.java))
        }
    }
}