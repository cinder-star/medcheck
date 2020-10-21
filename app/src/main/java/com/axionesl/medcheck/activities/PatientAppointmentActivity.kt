package com.axionesl.medcheck.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.Appointment
import com.axionesl.medcheck.domains.User
import com.axionesl.medcheck.utils.AppointmentAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.paperdb.Paper

class PatientAppointmentActivity : AppCompatActivity() {
    private lateinit var addAppointment: FloatingActionButton
    private lateinit var appointmentList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_appointment)
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
            ref.orderByChild("patientName").equalTo(name)
        val options = FirebaseRecyclerOptions.Builder<Appointment>()
            .setQuery(query, Appointment::class.java)
            .build()

        val adapter = AppointmentAdapter(this, options)
        appointmentList.adapter = adapter
        adapter.startListening()
    }


    private fun bindWidgets() {
        addAppointment = findViewById(R.id.add_appointment)
        appointmentList = findViewById(R.id.appointment_list)
    }

    private fun bindListeners() {
        addAppointment.setOnClickListener {
            startActivity(Intent(this, AddAppointmentActivity::class.java))
        }
    }
}