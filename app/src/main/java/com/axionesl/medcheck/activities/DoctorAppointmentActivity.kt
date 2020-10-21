package com.axionesl.medcheck.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.Appointment
import com.axionesl.medcheck.domains.User
import com.axionesl.medcheck.utils.DoctorAppointmentAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.paperdb.Paper

class DoctorAppointmentActivity : AppCompatActivity() {
    private lateinit var appointmentList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_appointment)
        title = "My appointments"
        bindWidgets()
        bindListeners()
    }
    override fun onStart() {
        super.onStart()
        appointmentList.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        prepareRecyclerView()
    }

    private fun prepareRecyclerView() {
        val name: String? = Paper.book().read<User>("user", null).fullName
        val ref = Firebase.database.reference.child("/appointments/")
        ref.keepSynced(true)
        val query =
            ref.orderByChild("doctorStatus").equalTo("Not Reviewed$name")
        val options = FirebaseRecyclerOptions.Builder<Appointment>()
            .setQuery(query, Appointment::class.java)
            .build()

        val adapter = DoctorAppointmentAdapter(this, options)
        appointmentList.adapter = adapter
        adapter.startListening()
    }


    private fun bindWidgets() {
        appointmentList = findViewById(R.id.doctor_appointment_list)
    }

    private fun bindListeners() {
    }
}