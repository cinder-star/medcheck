package com.axionesl.medcheck.activities

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.User
import com.axionesl.medcheck.repository.DatabaseWriter
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import io.paperdb.Paper

class EditProfileActivity : AppCompatActivity() {
    private lateinit var name: TextInputEditText
    private lateinit var mobileNumber: TextInputEditText
    private lateinit var saveUser: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        bindWidgets()
        bindListeners()
        updateData()
    }
    private fun bindWidgets() {
        name = findViewById(R.id.user_full_name)
        mobileNumber = findViewById(R.id.user_mobile_number)
        saveUser = findViewById(R.id.save_user)
    }

    private fun bindListeners() {
        saveUser.setOnClickListener {
            updateUser()
            finish()
        }
    }

    private fun updateUser() {
        val currentUser: User = Paper.book().read<User>("user", null)
        currentUser.fullName = name.text.toString()
        currentUser.mobileNumber = mobileNumber.text.toString()
        Paper.book().write("user", currentUser)
        DatabaseWriter.write("/user/"+currentUser.id, currentUser)
    }

    private fun updateData() {
        val reference = Firebase
            .database
            .reference
            .child("/user/")
            .orderByChild("id")
            .equalTo(Firebase.auth.currentUser!!.uid)
        reference.keepSynced(true)
        reference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val user: User? = it.getValue<User>()
                    updateUI(user)
                }
            }
        })
    }

    private fun updateUI(user: User?) {
        name.setText(user!!.fullName)
        mobileNumber.setText(user.mobileNumber)
        Paper.book().write("user", user)
    }
}