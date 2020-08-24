package com.axionesl.medcheck.activities

import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.Test
import com.axionesl.medcheck.domains.User
import com.axionesl.medcheck.repository.DatabaseWriter
import com.axionesl.medcheck.utils.AbstractTestDetailsActivity
import com.google.android.material.textfield.TextInputEditText
import io.paperdb.Paper


class CreatePrescriptionActivity :
    AbstractTestDetailsActivity(R.layout.activity_create_prescription) {
    private lateinit var prescription: TextInputEditText
    private lateinit var createPrescription: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindListeners()
    }

    override fun bindWidgets() {
        super.bindWidgets()
        prescription = findViewById(R.id.prescription)
        createPrescription = findViewById(R.id.create_prescription)
    }

    override fun bindListeners() {
        createPrescription.setOnClickListener {
            if (validate()) {
                val test = Paper.book().read<Test>("test")
                test.prescription = prescription.text.toString()
                test.status = "Checked"
                test.checkedBy = Paper.book().read<User>("user", null).fullName
                DatabaseWriter.write("/tests/" + test.id, test)
                val smsManager: SmsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(
                    "+88" + test.mobileNumber,
                    null,
                    createMessage(test),
                    null,
                    null
                )
                finish()
            }
        }
    }

    private fun createMessage(test: Test): String? {
        return "Your test (id: " + test.id + ") has been checked by " + test.checkedBy
    }

    private fun validate(): Boolean {
        if (prescription.text!!.isEmpty()) {
            prescription.error = "No prescription given"
            prescription.requestFocus()
            return false
        }
        return true
    }
}