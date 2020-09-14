package com.axionesl.medcheck.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.User
import com.bumptech.glide.signature.ObjectKey
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DoctorListAdapter(
    private val context: Context,
    options: FirebaseRecyclerOptions<User>
) :
    FirebaseRecyclerAdapter<User, DoctorListAdapter.ViewHolder>(
        options
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_doctor, parent, false)
        return ViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: User) {
        holder.bindView(model)
    }

    class ViewHolder(itemView: View, private val context: Context) :
        RecyclerView.ViewHolder(itemView) {
        fun bindView(user: User) {
            val doctorName = itemView.findViewById<TextView>(R.id.doctor_name)
            val doctorType = itemView.findViewById<TextView>(R.id.doctor_type)
            val doctorDegree = itemView.findViewById<TextView>(R.id.doctor_degree)
            val doctorDesignation = itemView.findViewById<TextView>(R.id.doctor_designation)
            val doctorPic = itemView.findViewById<ImageView>(R.id.doctor_photo)

            doctorName.text = user.fullName
            doctorType.text = user.doctorType
            doctorDegree.text = user.degree
            doctorDesignation.text = user.currentDesignation
            if (user.profilePicturePath != null) {
                val ref = Firebase.storage.reference.child("user/" + user.profilePicturePath)
                GlideApp
                    .with(context)
                    .load(ref)
                    .signature(ObjectKey(user.profilePicturePath+user.lastUpdated))
                    .into(doctorPic)
            }
        }
    }
}