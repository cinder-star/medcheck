package com.axionesl.medcheck.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import com.aditya.filebrowser.Constants
import com.aditya.filebrowser.FileChooser
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.User
import com.axionesl.medcheck.repository.DatabaseWriter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.paperdb.Paper


class SignUpActivity : AppCompatActivity() {
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var confirmPassword: TextInputEditText
    private lateinit var fullName: TextInputEditText
    private lateinit var mobileNumber: TextInputEditText
    private lateinit var bloodType: TextInputEditText
    private lateinit var accountType: AppCompatSpinner
    private lateinit var signUp: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var profilePicture: ImageView
    private lateinit var profilePictureAdd: FloatingActionButton
    private val auth = Firebase.auth
    private lateinit var uri: Uri
    private val PICK_FILE_REQUEST = 120


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
        mobileNumber = findViewById(R.id.mobile_number)
        bloodType = findViewById(R.id.blood_type)
        signUp = findViewById(R.id.sign_up)
        progressBar = findViewById(R.id.progress_bar)
        profilePicture = findViewById(R.id.profile_picture)
        profilePictureAdd = findViewById(R.id.add_profile_picture)
    }

    private fun bindListeners() {
        signUp.setOnClickListener {
            signUp.hideKeyboard()
            if (validate()) {
                progressBar.visibility = View.VISIBLE
                makeUser(email.text.toString(), password.text.toString())
            }
        }
        profilePictureAdd.setOnClickListener {
            val i = Intent(this, FileChooser::class.java)
            i.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal)
            startActivityForResult(i, PICK_FILE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                uri = data.data!!
                profilePicture.setImageURI(uri)
            }
        }
    }

    private fun validate(): Boolean {
        var result = true
        val textFields: ArrayList<TextInputEditText> =
            arrayListOf(email, password, confirmPassword, fullName)
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
        val regex = "(01[356789][0-9]{8})".toRegex()
        val input = mobileNumber.text.toString()
        if (!regex.matches(input)) {
            mobileNumber.error = "Invalid Mobile Number!"
            result = false
        }
        return result
    }

    private fun makeUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                val user = User(
                    auth.currentUser!!.uid,
                    email,
                    fullName.text.toString(),
                    mobileNumber.text.toString(),
                    accountType.selectedItem.toString(),
                    bloodType.text.toString()
                )
                Paper.book().write("account_type", user.accountType)
                DatabaseWriter.write("/user/" + auth.currentUser!!.uid, user)
                changeActivity(MainActivity::class.java)
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Sign Up failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun <T> changeActivity(jClass: Class<T>) {
        val i = Intent(this, jClass)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

    private fun View.hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}