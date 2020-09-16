package com.axionesl.medcheck.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.User
import com.axionesl.medcheck.utils.DoctorListAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DoctorViewActivity : AppCompatActivity() {
    private lateinit var doctorList: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_view)
        title = "Doctor List"
        bindWidgets()
    }

    private fun bindWidgets() {
        doctorList = findViewById(R.id.doctor_list)
    }

    override fun onStart() {
        super.onStart()
        doctorList.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        prepareRecyclerView()
    }

    private fun prepareRecyclerView() {
        val ref = Firebase.database.reference.child("/user/")
        ref.keepSynced(true)
        val query =
            ref.orderByChild("accountType").equalTo("Doctor")
        val options = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .build()

        val adapter = DoctorListAdapter(this, options)
        doctorList.adapter = adapter
        adapter.startListening()
    }
}