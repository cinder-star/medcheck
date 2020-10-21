package com.axionesl.medcheck.utils

import android.content.Context
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.Appointment
import com.axionesl.medcheck.repository.DatabaseWriter
import com.firebase.ui.database.FirebaseRecyclerOptions

class DoctorAppointmentAdapter(
    private val context: Context,
    options: FirebaseRecyclerOptions<Appointment>
) : AppointmentAdapter(context, options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.row_doctor_appointment, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : AppointmentAdapter.ViewHolder(itemView) {
        override fun bindView(appointment: Appointment) {
            super.bindView(appointment)
            val acceptButton: Button = itemView.findViewById(R.id.accept)
            val rejectButton: Button = itemView.findViewById(R.id.reject)
            acceptButton.setOnClickListener {
                appointment.status = "Accepted"
                appointment.doctorStatus = appointment.doctorName + "Accepted"
                DatabaseWriter.write("/appointments/" + appointment.id, appointment)
                sendSms(appointment)
            }
            rejectButton.setOnClickListener {
                appointment.status = "Rejected"
                appointment.doctorStatus = appointment.doctorName + "Rejected"
                DatabaseWriter.write("/appointments/" + appointment.id, appointment)
                sendSms(appointment)
            }
        }

        private fun sendSms(appointment: Appointment) {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(
                "+88" + appointment.patientNumber,
                null,
                createMessage(appointment),
                null,
                null
            )
        }

        private fun createMessage(appointment: Appointment): String {
            return "Your appointment "+
                    appointment.id+
                    " has been "+
                    appointment.status+
                    " by "+
                    appointment.doctorName+
                    ".\nAppintment Schedule:\nDate: "+
                    appointment.date+
                    "\nTime: "+
                    appointment.time
        }
    }
}