package com.axionesl.medcheck.utils

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axionesl.medcheck.domains.Test
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class PatientAdapter(options: FirebaseRecyclerOptions<Test>) : FirebaseRecyclerAdapter<Test, PatientAdapter.ViewHolder>(
    options
) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Test) {
        TODO("Not yet implemented")
    }
}