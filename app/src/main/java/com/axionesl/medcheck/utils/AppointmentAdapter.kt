package com.axionesl.medcheck.utils

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.Appointment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class AppointmentAdapter(private val context: Context, options: FirebaseRecyclerOptions<Appointment>) : FirebaseRecyclerAdapter<Appointment, AppointmentAdapter.ViewHolder>(
    options
) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(appointment: Appointment) {
            val patientName: TextView = itemView.findViewById(R.id.appointment_patient)
            val doctorName: TextView = itemView.findViewById(R.id.appointment_doctor)
            val status: TextView = itemView.findViewById(R.id.appointment_status)
            val date: TextView = itemView.findViewById(R.id.date)
            val time: TextView = itemView.findViewById(R.id.time)
            var colourString = "#808080"
            date.text = appointment.date
            time.text = appointment.time
            patientName.text = appointment.patientName
            doctorName.text = appointment.doctorName
            status.text = appointment.status

            colourString = when (appointment.status) {
                "Rejected" -> {
                    "#FF0000"
                }
                "Accepted" -> {
                    "#008000"
                }
                else -> {
                    "#808080"
                }
            }

            status.setTextColor(Color.parseColor(colourString))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_appointment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Appointment) {
        holder.bindView(model)
    }
}