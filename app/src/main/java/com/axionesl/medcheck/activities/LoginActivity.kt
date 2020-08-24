package com.axionesl.medcheck.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import io.paperdb.Paper

class LoginActivity : AppCompatActivity() {
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var logIn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var signUpRedirect: TextView
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        bindWidgets()
        bindListeners()
    }

    private fun bindWidgets() {
        email = findViewById(R.id.credential_value)
        password = findViewById(R.id.password_value)
        logIn = findViewById(R.id.log_in)
        progressBar = findViewById(R.id.progress_bar)
        signUpRedirect = findViewById(R.id.redirect)
    }

    private fun bindListeners() {
        logIn.setOnClickListener {
            logIn.hideKeyboard()
            if (validate()) {
                progressBar.visibility = View.VISIBLE
                loginUser(email.text.toString(), password.text.toString())
            }
        }
        signUpRedirect.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun validate(): Boolean {
        var result = true
        val textFields: ArrayList<TextInputEditText> =
            arrayListOf(email, password)
        textFields.forEach { e ->
            if (e.text.toString().isEmpty()) {
                e.error = "Field cannot be empty"
                e.requestFocus()
                result = false
            }
        }
        return result
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                changeActivity()
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun changeActivity() {
        val userReference = Firebase.database.reference.child("/user/")
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val user = it.getValue<User>()
                    if (user!!.id == Firebase.auth.currentUser!!.uid) {
                        Paper.book().write("user", user)
                        progressBar.visibility = View.GONE
                        changeActivity(MainActivity::class.java)
                    }
                }
            }

            fun <T> changeActivity(jClass: Class<T>) {
                val i = Intent(this@LoginActivity, jClass)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
            }
        })

    }

    private fun View.hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}