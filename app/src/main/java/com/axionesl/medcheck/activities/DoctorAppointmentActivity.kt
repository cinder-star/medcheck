package com.axionesl.medcheck.activities

import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.Appointment
import com.axionesl.medcheck.domains.User
import com.axionesl.medcheck.utils.DoctorAppointmentAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.paperdb.Paper

class DoctorAppointmentActivity : AppCompatActivity() {
    private lateinit var appointmentList: RecyclerView
    private lateinit var appointmentFilter: RadioGroup
    private lateinit var ref: DatabaseReference
    private lateinit var pendingAdapter: DoctorAppointmentAdapter
    private lateinit var acceptedAdapter: DoctorAppointmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_appointment)
        title = "My appointments"
        bindWidgets()
        bindListeners()
    }

    override fun onStart() {
        super.onStart()
        prepareRecyclerView()
    }

    private fun prepareRecyclerView() {
        appointmentList.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        appointmentList.adapter = pendingAdapter
        pendingAdapter.startListening()
    }


    private fun bindWidgets() {
        appointmentList = findViewById(R.id.doctor_appointment_list)
        appointmentFilter = findViewById(R.id.appointment_filter)
        ref = Firebase.database.reference.child("/appointments/")
        ref.keepSynced(true)
        val name: String? = Paper.book().read<User>("user", null).fullName
        val pendingQuery =
            ref.orderByChild("doctorStatus").equalTo("Not Reviewed$name")
        val acceptedQuery =
            ref.orderByChild("doctorStatus").equalTo("Accepted$name")
        val pendingOptions = FirebaseRecyclerOptions.Builder<Appointment>()
            .setQuery(pendingQuery, Appointment::class.java)
            .build()
        val acceptedOptions = FirebaseRecyclerOptions.Builder<Appointment>()
            .setQuery(acceptedQuery, Appointment::class.java)
            .build()
        pendingAdapter = DoctorAppointmentAdapter(this, pendingOptions, "pending")
        acceptedAdapter = DoctorAppointmentAdapter(this, acceptedOptions, "accepted")
    }

    private fun bindListeners() {
        appointmentFilter.setOnCheckedChangeListener { _, i ->
            val radioButton: RadioButton = findViewById(i)
            val text = radioButton.text.toString()
            Log.e("button", text)
            if (text == "Scheduled") {
                pendingAdapter.stopListening()
                appointmentList.layoutManager =
                    StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                appointmentList.adapter = acceptedAdapter
                acceptedAdapter.startListening()
            } else {
                acceptedAdapter.stopListening()
                appointmentList.layoutManager =
                    StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                appointmentList.adapter = pendingAdapter
                pendingAdapter.startListening()
            }
        }
    }
}