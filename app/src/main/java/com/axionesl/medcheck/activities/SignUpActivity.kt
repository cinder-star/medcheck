package com.axionesl.medcheck.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
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
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.User
import com.axionesl.medcheck.repository.DatabaseWriter
import com.axionesl.medcheck.repository.StorageWriter
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.paperdb.Paper
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class SignUpActivity : AppCompatActivity() {
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var confirmPassword: TextInputEditText
    private lateinit var fullName: TextInputEditText
    private lateinit var mobileNumber: TextInputEditText
    private lateinit var bloodType: TextInputEditText
    private lateinit var accountType: RadioGroup
    private lateinit var doctorType: AppCompatSpinner
    private lateinit var signUp: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var profilePicture: ImageView
    private lateinit var profilePictureAdd: FloatingActionButton
    private lateinit var birthday: TextView
    private lateinit var chooseDate: ImageButton
    private lateinit var degree: TextInputEditText
    private lateinit var currentDesignation: TextInputEditText
    private lateinit var degreeHolder: TextInputLayout
    private lateinit var currentDesignationHolder: TextInputLayout
    private val auth = Firebase.auth
    private var uri: Uri? = null

    @Suppress("PrivatePropertyName")
    private val RESULT_LOAD_IMAGE = 1


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
        birthday = findViewById(R.id.birthday)
        chooseDate = findViewById(R.id.choose_date)
        doctorType = findViewById(R.id.appCompatSpinner)
        degree = findViewById(R.id.degree)
        currentDesignation = findViewById(R.id.current_designation)
        degreeHolder = findViewById(R.id.textInputLayout7)
        currentDesignationHolder = findViewById(R.id.textInputLayout8)
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
        accountType.setOnCheckedChangeListener { _, i ->
            val radioButton: RadioButton = findViewById(i)
            val text = radioButton.text.toString()
            if (text == "Doctor") {
                changeVisibility(View.VISIBLE)
            } else {
                changeVisibility(View.GONE)
            }
        }
    }

    private fun changeVisibility(visibility: Int) {
        doctorType.visibility = visibility
        degree.visibility = visibility
        degreeHolder.visibility = visibility
        currentDesignation.visibility = visibility
        currentDesignationHolder.visibility = visibility
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                profilePicture.setImageURI(uri)
                uri = data.data
                @Suppress("DEPRECATION")
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
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
        if (uri == null) {
            Toast.makeText(this, "No Profile picture chosen", Toast.LENGTH_LONG).show()
            result = false
        }
        return result
    }

    @SuppressLint("SimpleDateFormat")
    private fun makeUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                val id = Firebase.auth.currentUser!!.uid
                val user = prepareUserData(id, email)
                Paper.book().write("user", user)
                DatabaseWriter.write("/user/$id", user)
                StorageWriter.upload(
                    "/user/$id.jpg", getByteData(),
                    OnSuccessListener { changeActivity(MainActivity::class.java) },
                    null
                )
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Sign Up failed", Toast.LENGTH_SHORT).show()
            }
    }

    @SuppressLint("SimpleDateFormat")
    private fun prepareUserData(id: String, email: String): User {
        @Suppress("SpellCheckingInspection")
        val time = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        var userDoctorType: String? = null
        var userDegree: String? = null
        var userCurrentDesignation: String? = null
        if (findViewById<RadioButton>(accountType.checkedRadioButtonId).text.toString() == "Doctor") {
            userDoctorType = doctorType.selectedItem.toString()
            userDegree = degree.text.toString()
            userCurrentDesignation = currentDesignation.text.toString()
        }
        return User(
            auth.currentUser!!.uid,
            email,
            fullName.text.toString(),
            mobileNumber.text.toString(),
            findViewById<RadioButton>(accountType.checkedRadioButtonId).text.toString(),
            bloodType.text.toString(),
            birthday.text.toString(),
            "$id.jpg",
            time,
            userDoctorType,
            userDegree,
            userCurrentDesignation
        )
    }

    private fun getByteData(): ByteArray {
        @Suppress("DEPRECATION") var bitmap = if (Build.VERSION.SDK_INT >= 29) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.contentResolver, uri!!))
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