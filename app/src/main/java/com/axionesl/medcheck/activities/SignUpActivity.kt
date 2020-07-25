package com.axionesl.medcheck.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import com.axionesl.medcheck.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var confirmPassword: TextInputEditText
    private lateinit var fullName: TextInputEditText
    private lateinit var accountType: AppCompatSpinner
    private lateinit var signUp: Button
    private val auth = Firebase.auth
    private val database = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        bindWidgets()
        bindListeners()
    }

    private fun bindWidgets() {
        email = findViewById(R.id.credential)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirm_password)
        fullName = findViewById(R.id.full_name)
        accountType = findViewById(R.id.account_type)
        signUp = findViewById(R.id.sign_up)
    }

    private fun bindListeners() {
        signUp.setOnClickListener {
            if (validate()) {
                val i = Intent(this, MainActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
            }
        }
    }

    private fun validate(): Boolean {
        var result = true
        val textFields: ArrayList<TextInputEditText> = arrayListOf(email, password, confirmPassword, fullName)
        textFields.forEach { e ->
            if (e.text.toString().isEmpty()) {
                e.error = "Field cannot be empty"
                e.requestFocus()
                result = false
            }
        }
        if (!result) {
            return result
        }
        if (password.text.toString() != confirmPassword.text.toString()) {
            password.error = "Passwords do not match"
            confirmPassword.error = "Passwords do not match"
            password.requestFocus()
            confirmPassword.requestFocus()
            result = false
        }
        return result
    }
}