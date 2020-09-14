package com.axionesl.medcheck.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.User
import com.axionesl.medcheck.utils.GlideApp
import com.bumptech.glide.signature.ObjectKey
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.paperdb.Paper

class ProfileActivity : AppCompatActivity() {
    private lateinit var name: TextView
    private lateinit var email: TextView
    private lateinit var accountType: TextView
    private lateinit var mobileNumber: TextView
    private lateinit var bloodType: TextView
    private lateinit var logOut: Button
    private lateinit var profilePicture: ImageView
    private lateinit var birthDate: TextView
    private lateinit var doctorType: TextView
    private lateinit var degree: TextView
    private lateinit var currentDesignation: TextView
    private lateinit var doctorTypeHolder: LinearLayout
    private lateinit var degreeHolder: LinearLayout
    private lateinit var currentDesignationHolder: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        bindWidgets()
        bindListeners()
    }

    private fun bindWidgets() {
        name = findViewById(R.id.user_full_name)
        email = findViewById(R.id.user_email)
        mobileNumber = findViewById(R.id.user_mobile_number)
        accountType = findViewById(R.id.user_account_type)
        bloodType = findViewById(R.id.user_blood_type)
        logOut = findViewById(R.id.log_out)
        profilePicture = findViewById(R.id.profile_picture)
        birthDate = findViewById(R.id.user_birth_date)
        degree = findViewById(R.id.user_degree)
        doctorType = findViewById(R.id.user_doctor_type)
        currentDesignation = findViewById(R.id.user_current_designation)
        degreeHolder = findViewById(R.id.degree_holder)
        doctorTypeHolder = findViewById(R.id.doctor_type_holder)
        currentDesignationHolder = findViewById(R.id.current_designation_holder)
    }

    private fun bindListeners() {
        logOut.setOnClickListener {
            Firebase.auth.signOut()
            Paper.book().write("user", "")
            changeActivity(LoginActivity::class.java)
        }
    }

    override fun onStart() {
        super.onStart()
        val reference = Firebase
            .database
            .reference
            .child("/user/")
            .orderByChild("id")
            .equalTo(Firebase.auth.currentUser!!.uid)
        reference.keepSynced(true)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val user: User? = it.getValue<User>()
                    updateUI(user)
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(user: User?) {
        name.text = user!!.fullName
        email.text = user.email
        mobileNumber.text = user.mobileNumber
        accountType.text = user.accountType
        bloodType.text = user.bloodType
        birthDate.text = user.dateOfBirth
        if (user.profilePicturePath != null) {
            val picRef =
                Firebase.storage.reference.child("/user/" + Firebase.auth.currentUser!!.uid + ".jpg")
            GlideApp
                .with(this)
                .load(picRef)
                .signature(ObjectKey(user.profilePicturePath + user.lastUpdated))
                .into(profilePicture)
        }
        if (user.accountType == "Doctor") {
            degreeHolder.visibility = View.VISIBLE
            doctorTypeHolder.visibility = View.VISIBLE
            currentDesignationHolder.visibility = View.VISIBLE
            degree.text = user.degree
            doctorType.text = user.doctorType
            currentDesignation.text = user.currentDesignation
        }
    }

    private fun <T> changeActivity(jClass: Class<T>) {
        val i = Intent(this, jClass)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.profile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_profile -> {
                startActivity(Intent(this, EditProfileActivity::class.java))
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}