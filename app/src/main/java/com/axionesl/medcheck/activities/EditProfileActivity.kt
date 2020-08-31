package com.axionesl.medcheck.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.aditya.filebrowser.Constants
import com.aditya.filebrowser.FileChooser
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.User
import com.axionesl.medcheck.repository.DatabaseWriter
import com.axionesl.medcheck.repository.StorageWriter
import com.axionesl.medcheck.utils.GlideApp
import com.bumptech.glide.signature.ObjectKey
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.paperdb.Paper
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    private lateinit var name: TextInputEditText
    private lateinit var mobileNumber: TextInputEditText
    private lateinit var bloodType: TextInputEditText
    private lateinit var saveUser: Button
    private lateinit var profilePicture: ImageView
    private lateinit var profilePictureAdd: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    private lateinit var birthday: TextView
    private lateinit var chooseDate: ImageButton
    private var uri: Uri? = null
    private val fileRequest = 120
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
        bloodType = findViewById(R.id.user_blood_type)
        saveUser = findViewById(R.id.save_user)
        profilePicture = findViewById(R.id.profile_picture)
        profilePictureAdd = findViewById(R.id.add_profile_picture)
        progressBar = findViewById(R.id.progress_bar)
        birthday = findViewById(R.id.birthday)
        chooseDate = findViewById(R.id.choose_date)
    }

    private fun bindListeners() {
        saveUser.setOnClickListener {
            if (validate()) {
                progressBar.visibility = View.VISIBLE
                updateUser()
            }
        }
        profilePictureAdd.setOnClickListener {
            val i = Intent(this, FileChooser::class.java)
            i.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal)
            startActivityForResult(i, fileRequest)
        }
        chooseDate.setOnClickListener {
            val currentDate = Calendar.getInstance()
            val mYear = currentDate[Calendar.YEAR]
            val mMonth = currentDate[Calendar.MONTH]
            val mDay = currentDate[Calendar.DAY_OF_MONTH]
            startChooserDialogue(mDay, mMonth, mYear)
        }
    }

    private fun startChooserDialogue(mDay: Int, mMonth: Int, mYear: Int) {
        val mDatePicker = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val myCalendar = Calendar.getInstance()
                myCalendar[Calendar.YEAR] = selectedYear
                myCalendar[Calendar.MONTH] = selectedMonth
                myCalendar[Calendar.DAY_OF_MONTH] = selectedDay
                val myFormat = "dd-MM-yy" //Change as you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                birthday.text = sdf.format(myCalendar.time)
            }, mYear, mMonth, mDay)
        mDatePicker.setTitle("Select date")
        mDatePicker.show()
    }

    private fun validate(): Boolean {
        var result = true
        val textFields: ArrayList<TextInputEditText> =
            arrayListOf(name)
        textFields.forEach { e ->
            if (e.text.toString().isEmpty()) {
                e.error = "Field cannot be empty"
                e.requestFocus()
                result = false
            }
        }
        val regex = "(01[356789][0-9]{8})".toRegex()
        val input = mobileNumber.text.toString()
        if (!regex.matches(input)) {
            mobileNumber.error = "Invalid Mobile Number!"
            result = false
        }
        return result
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == fileRequest && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                uri = data.data!!
                profilePicture.setImageURI(uri)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateUser() {
        val currentUser: User = Paper.book().read<User>("user", null)
        currentUser.fullName = name.text.toString()
        currentUser.mobileNumber = mobileNumber.text.toString()
        currentUser.bloodType = bloodType.text.toString()
        currentUser.dateOfBirth = birthday.text.toString()
        if (uri != null) {
            @Suppress("SpellCheckingInspection")
            val time = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
            currentUser.lastUpdated = time
            currentUser.profilePicturePath = currentUser.id+".jpg"
            StorageWriter.upload(
                "/user/" + currentUser.id + ".jpg", getByteData(uri!!),
                OnSuccessListener {
                    writeUserInfoIntoDatabase(currentUser)
                }, null
            )
        } else {
           writeUserInfoIntoDatabase(currentUser)
        }
    }

    private fun writeUserInfoIntoDatabase(currentUser: User) {
        Paper.book().write("user", currentUser)
        DatabaseWriter.write("/user/" + currentUser.id, currentUser)
        progressBar.visibility = View.GONE
        finish()
    }

    private fun updateData() {
        val reference = Firebase
            .database
            .reference
            .child("/user/")
            .orderByChild("id")
            .equalTo(Firebase.auth.currentUser!!.uid)
        reference.keepSynced(true)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
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
        name.setText(user!!.fullName)
        mobileNumber.setText(user.mobileNumber)
        if (user.bloodType != null) {
            bloodType.setText(user.bloodType)
        } else {
            bloodType.setText("none")
        }
        Paper.book().write("user", user)
        if (user.profilePicturePath != null) {
            val photoRef =
                Firebase.storage.reference.child("/user/" + user.profilePicturePath)
            GlideApp
                .with(this)
                .load(photoRef)
                .signature(ObjectKey(user.profilePicturePath + user.lastUpdated))
                .into(profilePicture)
        }
    }

    private fun getByteData(uri: Uri): ByteArray {
        @Suppress("DEPRECATION") var bitmap = if (Build.VERSION.SDK_INT >= 29) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.contentResolver, uri))
        } else {
            MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        }
        val byteOutputStream = ByteArrayOutputStream()
        bitmap = Bitmap.createScaledBitmap(
            bitmap, 1024,
            ((bitmap.height * (1024.0 / bitmap.width)).toInt()), true
        )
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteOutputStream)
        return byteOutputStream.toByteArray()
    }
}