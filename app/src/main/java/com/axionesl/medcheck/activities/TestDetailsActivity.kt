package com.axionesl.medcheck.activities

import android.widget.TextView
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.Test
import com.axionesl.medcheck.utils.AbstractTestDetailsActivity

class TestDetailsActivity : AbstractTestDetailsActivity(R.layout.activity_test_details) {
    private lateinit var prescription: TextView
    override fun bindWidgets() {
        super.bindWidgets()
        prescription = findViewById(R.id.test_prescription)
    }

    override fun updateUI(test: Test?) {
        super.updateUI(test)
        prescription.text = test!!.prescription
    }

    override fun bindListeners() {
    }
}