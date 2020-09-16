package com.axionesl.medcheck.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.User
import com.axionesl.medcheck.repository.DatabaseWriter
import com.axionesl.medcheck.repository.StorageWriter
import com.axionesl.medcheck.utils.GlideApp
import com.bumptech.glide.signature.ObjectKey
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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
    private lateinit var degree: TextInputEditText
    private lateinit var doctorType: AppCompatSpinner
    private lateinit var currentDesignation: TextInputEditText
    private lateinit var degreeHolder: TextInputLayout
    private lateinit var currentDesignationHolder: TextInputLayout

    @Suppress("PrivatePropertyName")
    private val RESULT_LOAD_IMAGE = 1
    private var uri: Uri? = null
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
        degree = findViewById(R.id.user_degree)
        currentDesignation = findViewById(R.id.user_current_designation)
        doctorType = findViewById(R.id.user_doctor_type)
        degreeHolder = findViewById(R.id.degree_holder)
        currentDesignationHolder = findViewById(R.id.current_designation_holder)
    }

    private fun bindListeners() {
        saveUser.setOnClickListener {
            if (validate()) {
                progressBar.visibility = View.VISIBLE
                updateUser()
            }
        }
        profilePictureAdd.setOnClickListener {
            val i = Intent(
                Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(i, RESULT_LOAD_IMAGE)
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
        val mDatePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val myCalendar = Calendar.getInstance()
                myCalendar[Calendar.YEAR] = selectedYear
                myCalendar[Calendar.MONTH] = selectedMonth
                myCalendar[Calendar.DAY_OF_MONTH] = selectedDay
                val myFormat = "dd-MM-yy" //Change as you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                birthday.text = sdf.format(myCalendar.time)
            }, mYear, mMonth, mDay
        )
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
        if (requestCode == RESULT_LOAD_IMAGE && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                profilePicture.setImageURI(uri)
                uri = data.data
                @Suppress("DEPRECATION") val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val cursor: Cursor? = contentResolver.query(
                    uri!!,
                    filePathColumn, null, null, null
                )
                cursor!!.moveToFirst()
                val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                val picturePath: String = cursor.getString(columnIndex)
                cursor.close()
                profilePicture.setImageBitmap(BitmapFactory.decodeFile(picturePath))
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
        if (currentUser.accountType == "Doctor") {
            currentUser.degree = degree.text.toString()
            currentUser.currentDesignation = currentDesignation.text.toString()
            currentUser.doctorType = doctorType.selectedItem.toString()
        }
        if (uri != null) {
            @Suppress("SpellCheckingInspection")
            val time = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
            currentUser.lastUpdated = time
            currentUser.profilePicturePath = currentUser.id + ".jpg"
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
        if (user.dateOfBirth != null) {
            birthday.text = user.dateOfBirth
        }
        if (user.accountType == "Doctor") {
            degreeHolder.visibility = View.VISIBLE
            currentDesignationHolder.visibility = View.VISIBLE
            doctorType.visibility = View.VISIBLE
            degree.setText(user.degree)
            currentDesignation.setText(user.currentDesignation)
            val compareValue = user.doctorType
            val adapter = ArrayAdapter.createFromResource(
                this,
                R.array.speciality_type,
                android.R.layout.simple_spinner_item
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            doctorType.adapter = adapter
            if (compareValue != null) {
                val spinnerPosition = adapter.getPosition(compareValue)
                doctorType.setSelection(spinnerPosition)
            }
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