package com.axionesl.medcheck.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.axionesl.medcheck.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var email: TextInputEditText
    private lateinit var send: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        title = "Account recovery"
        bindWidgets()
        bindListeners()
    }

    private fun bindWidgets() {
        email = findViewById(R.id.credential_value)
        send = findViewById(R.id.send_mail)
    }

    private fun bindListeners() {
        send.setOnClickListener {
            if (validate()) {
                Firebase.auth.sendPasswordResetEmail(email.text.toString())
                    .addOnSuccessListener {
                        Toast.makeText(this,"A recovery mail has been sent!", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"Failed!", Toast.LENGTH_LONG).show()
                    }
                    .addOnCompleteListener {
                        finish()
                    }
            }
        }
    }

    private fun validate(): Boolean {
        if (email.text.toString().isEmpty()) {
            return false
        }
        return true
    }
}