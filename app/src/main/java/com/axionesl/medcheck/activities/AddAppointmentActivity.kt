package com.axionesl.medcheck.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.Appointment
import com.axionesl.medcheck.domains.User
import com.axionesl.medcheck.repository.DatabaseWriter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import io.paperdb.Paper
import java.text.SimpleDateFormat
import java.util.*

class AddAppointmentActivity : AppCompatActivity() {
    private lateinit var doctorType: AppCompatSpinner
    private lateinit var preferredDoctor: AppCompatSpinner
    private lateinit var chooseDate: ImageButton
    private lateinit var chooseTime: ImageButton
    private lateinit var date: TextView
    private lateinit var time: TextView
    private lateinit var createAppointment: Button
    private var chosenType: String = "Medicine"
    private var doctorList: ArrayList<String> = arrayListOf("None")
    private var preferredDoctorValue = "None"
    private lateinit var doctorListener: ValueEventListener
    private lateinit var reference: Query

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_appointment)
        title = "Request new Appointment"
        bindWidgets()
        bindListeners()
    }

    private fun bindWidgets() {
        chooseDate = findViewById(R.id.choose_date)
        chooseTime = findViewById(R.id.choose_time)
        doctorType = findViewById(R.id.doctor_type)
        date = findViewById(R.id.date)
        time = findViewById(R.id.time)
        createAppointment = findViewById(R.id.create_appointment)
        preferredDoctor = findViewById(R.id.preferred_doctor)
        reference = Firebase.database.reference
            .child("user")
            .orderByChild("accountType")
            .equalTo("Doctor")
        doctorListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                doctorList = arrayListOf("None")
                snapshot.children.forEach {
                    val name = it.child("fullName").getValue<String>()
                    val type = it.child("doctorType").getValue<String>()
                    if (type == chosenType) {
                        doctorList.add(name!!)
                    }
                }
                val doctorAdapter: ArrayAdapter<String> = ArrayAdapter(
                    this@AddAppointmentActivity,
                    android.R.layout.simple_spinner_item,
                    doctorList
                )
                preferredDoctor.adapter = doctorAdapter
            }

            override fun onCancelled(error: DatabaseError) {}
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun bindListeners() {
        doctorType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                preferredDoctorValue = "None"
                chosenType = doctorType.selectedItem.toString()
                reference.removeEventListener(doctorListener)
                reference.addListenerForSingleValueEvent(doctorListener)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        chooseDate.setOnClickListener {
            val currentDate = Calendar.getInstance()
            val mYear = currentDate[Calendar.YEAR]
            val mMonth = currentDate[Calendar.MONTH]
            val mDay = currentDate[Calendar.DAY_OF_MONTH]
            startChooserDialogue(mDay, mMonth, mYear)
        }

        chooseTime.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val mHour = currentTime[Calendar.HOUR_OF_DAY]
            val mMinute = currentTime[Calendar.MINUTE]
            startChooseTimeDialogue(mHour, mMinute)
        }

        createAppointment.setOnClickListener {
            preferredDoctorValue = preferredDoctor.selectedItem.toString()
            if (validate()) {
                val dateStamp = SimpleDateFormat("ddMMyyyy").format(Date())
                DatabaseWriter.write("appointments/$dateStamp", getAppointment())
                finish()
            }
        }
    }

    private fun getAppointment(): Appointment {
        val user: User? = Paper.book().read("user", null)
        return Appointment(
            preferredDoctorValue,
            user!!.fullName,
            user.mobileNumber,
            date.text.toString(),
            time.text.toString(),
            "Not Reviewed"
        )
    }

    private fun validate(): Boolean {
        if (preferredDoctorValue == "None") {
            return false
        }
        return true
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
                date.text = sdf.format(myCalendar.time)
            }, mYear, mMonth, mDay
        )
        mDatePicker.setTitle("Select date")
        mDatePicker.show()
    }

    private fun startChooseTimeDialogue(mHour: Int, mMinute: Int) {
        val mTimePicker = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val selectedTime = "$hourOfDay:$minute"
                time.text = selectedTime
            }, mHour, mMinute, false
        )
        mTimePicker.setTitle("Select time")
        mTimePicker.show()
    }
}