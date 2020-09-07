package com.axionesl.medcheck.activities

import android.annotation.SuppressLint
import android.app.Activity
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
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.Test
import com.axionesl.medcheck.domains.User
import com.axionesl.medcheck.repository.DatabaseWriter
import com.axionesl.medcheck.repository.StorageWriter
import com.google.android.gms.tasks.OnFailureListener
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

class AddTestActivity : AppCompatActivity() {
    private lateinit var heightCm: TextInputEditText
    private lateinit var heightCmHolder: TextInputLayout
    private lateinit var heightFeet: LinearLayout
    private lateinit var heightFtValue: TextInputEditText
    private lateinit var heightInchValue: TextInputEditText
    private lateinit var weight: TextInputEditText
    private lateinit var bpm: TextInputEditText
    private lateinit var glucoseLevel: TextInputEditText
    private lateinit var oxygenLevel: TextInputEditText
    private lateinit var bloodPressure: TextInputEditText
    private lateinit var temperature: TextInputEditText
    private lateinit var problemDetails: TextInputEditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var createTest: Button
    private lateinit var testPhoto: ImageView
    private lateinit var addTestPhoto: FloatingActionButton
    private val RESULT_LOAD_IMAGE = 1
    private var uri: Uri? = null
    private var test: Test? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_test)
        bindWidgets()
        bindListeners()
    }

    private fun bindWidgets() {
        heightCm = findViewById(R.id.height_cm)
        heightCmHolder = findViewById(R.id.height_cm_holder)
        heightFeet = findViewById(R.id.height_ft_inch)
        radioGroup = findViewById(R.id.height)
        createTest = findViewById(R.id.create_test)
        weight = findViewById(R.id.weight)
        bpm = findViewById(R.id.bpm)
        glucoseLevel = findViewById(R.id.glucose_level)
        oxygenLevel = findViewById(R.id.oxygen_level)
        bloodPressure = findViewById(R.id.blood_pressure)
        temperature = findViewById(R.id.temperature)
        problemDetails = findViewById(R.id.problem_details)
        heightFtValue = findViewById(R.id.height_feet)
        heightInchValue = findViewById(R.id.height_inch)
        testPhoto = findViewById(R.id.test_photo)
        addTestPhoto = findViewById(R.id.add_test_photo)
        if (intent.extras != null) {
            test = intent.extras!!.get("test") as Test
        }
    }

    private fun bindListeners() {
        radioGroup.setOnCheckedChangeListener { _, i ->
            val radioButton: RadioButton = findViewById(i)
            val text = radioButton.text.toString()
            if (text == "Cm") {
                heightFeet.visibility = View.GONE
                heightCm.visibility = View.VISIBLE
                heightCmHolder.visibility = View.VISIBLE
            } else {
                heightFeet.visibility = View.VISIBLE
                heightCm.visibility = View.GONE
                heightCmHolder.visibility = View.GONE
            }
        }
        createTest.setOnClickListener {
            if (validate()) {
                val test: Test = getTestData()
                DatabaseWriter.write("/tests/" + test.id, test)
                if (test.testPicture != null) {
                    StorageWriter.upload(
                        "/tests/" + test.testPicture,
                        getByteData(uri!!),
                        OnSuccessListener {
                            finish()
                        },
                        OnFailureListener { finish() })
                } else {
                    finish()
                }
            }
        }
        addTestPhoto.setOnClickListener {
            val i = Intent(
                Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(i, RESULT_LOAD_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                testPhoto.setImageURI(uri)
                uri = data.data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val cursor: Cursor? = contentResolver.query(
                    uri!!,
                    filePathColumn, null, null, null
                )
                cursor!!.moveToFirst()
                val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                val picturePath: String = cursor.getString(columnIndex)
                cursor.close()
                testPhoto.setImageBitmap(BitmapFactory.decodeFile(picturePath))
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

    @SuppressLint("SimpleDateFormat")
    private fun getTestData(): Test {
        @Suppress("SpellCheckingInspection")
        var id = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        var testPhoto: String? = null
        if (test != null) {
            id = test!!.id!!
            testPhoto = test!!.testPicture!!
        }
        if (uri != null) {
            testPhoto = "$id.jpg"
        }
        val date = SimpleDateFormat("dd-MM-yyyy").format(Date())
        val patient: String? = Firebase.auth.currentUser!!.uid
        val mobileNumber: String? = Paper.book().read<User>("user").mobileNumber
        var details: String? = null
        if (problemDetails.text.toString().isNotEmpty()) {
            details = problemDetails.text.toString()
        }
        return Test(
            id,
            weight.text.toString().toDouble(),
            getHeight(),
            getBmi(),
            bpm.text.toString().toDouble(),
            bloodPressure.text.toString().toDouble(),
            glucoseLevel.text.toString().toDouble(),
            oxygenLevel.text.toString().toDouble(),
            temperature.text.toString().toDouble(),
            details,
            "In Queue",
            null,
            patient,
            date,
            null,
            mobileNumber,
            testPhoto
        )
    }

    private fun getBmi(): Double {
        var height: Double
        val buttonId = radioGroup.checkedRadioButtonId
        val radioButton: RadioButton = findViewById(buttonId)
        val text = radioButton.text.toString()
        if (text == "Cm") {
            height = heightCm.text.toString().toDouble() * 0.01
        } else {
            height =
                heightFtValue.text.toString().toDouble() * 12.0 + heightInchValue.text.toString()
                    .toDouble()
            height *= 0.0254
        }
        return (weight.text.toString().toDouble()) / height
    }

    private fun validate(): Boolean {
        var value = true
        val texts: ArrayList<TextInputEditText> =
            arrayListOf(weight, bpm, bloodPressure, glucoseLevel, oxygenLevel, temperature)
        texts.forEach {
            if (it.text?.isEmpty()!!) {
                it.error = "Cannot be empty!"
                it.requestFocus()
                value = false
            }
        }
        val buttonId = radioGroup.checkedRadioButtonId
        val radioButton: RadioButton = findViewById(buttonId)
        val text = radioButton.text.toString()
        if (text == "Cm") {
            if (heightCm.text!!.isEmpty()) {
                heightCm.error = "Cannot be empty!"
                heightCm.requestFocus()
                value = false
            }
        } else {
            if (heightFtValue.text!!.isEmpty()) {
                heightFtValue.error = "Cannot be empty!"
                heightFtValue.requestFocus()
                value = false
            }
            if (heightInchValue.text!!.isEmpty()) {
                heightInchValue.error = "Cannot be empty!"
                heightInchValue.requestFocus()
                value = false
            }
        }
        return value
    }

    private fun getHeight(): String {
        val buttonId = radioGroup.checkedRadioButtonId
        val radioButton: RadioButton = findViewById(buttonId)
        val text = radioButton.text.toString()
        return if (text == "Cm") {
            heightCm.text.toString() + "cm"
        } else {
            heightFtValue.text.toString() + "ft. " + heightInchValue.text.toString() + "in."
        }
    }
}