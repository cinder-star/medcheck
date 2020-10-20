package com.axionesl.medcheck.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import com.axionesl.medcheck.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AddAppointmentActivity : AppCompatActivity() {
    private lateinit var doctorType: AppCompatSpinner
    private lateinit var preferredDoctor: AppCompatSpinner
    private lateinit var chooseDate: ImageButton
    private lateinit var date: TextView
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
        doctorType = findViewById(R.id.doctor_type)
        date = findViewById(R.id.date)
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
}