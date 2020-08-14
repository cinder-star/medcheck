package com.axionesl.medcheck.utils

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.Test
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class PatientAdapter(private val context: Context, options: FirebaseRecyclerOptions<Test>) :
    FirebaseRecyclerAdapter<Test, PatientAdapter.ViewHolder>(
        options
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_test, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Test) {
        holder.bindView(model)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(test: Test) {
            val testId: TextView = itemView.findViewById(R.id.test_id)
            val testStatus: TextView = itemView.findViewById(R.id.test_status)
            val testCheckedBy: TextView = itemView.findViewById(R.id.test_checked_by)
            val testDate: TextView = itemView.findViewById(R.id.date)

            testId.text = test.id
            testStatus.text = test.status
            testDate.text = test.date
            if (test.checkedBy != null) {
                testCheckedBy.text = test.checkedBy
            }
            if (test.status == "In Queue") {
                testStatus.setTextColor(Color.parseColor("#FF0000"))
            } else {
                testStatus.setTextColor(Color.parseColor("#008000"))
            }
        }
    }
}