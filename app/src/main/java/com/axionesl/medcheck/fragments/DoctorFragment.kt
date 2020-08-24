package com.axionesl.medcheck.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.axionesl.medcheck.R
import com.axionesl.medcheck.activities.CreatePrescriptionActivity
import com.axionesl.medcheck.domains.Test
import com.axionesl.medcheck.utils.PatientAdapter
import com.axionesl.medcheck.utils.TestClickListener
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.paperdb.Paper

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DoctorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DoctorFragment : Fragment(), TestClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var testList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_doctor, container, false)
        bindWidgets(view)
        updateRecyclerView()
        return view
    }

    private fun updateRecyclerView() {
        testList.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        prepareRecyclerView()
    }

    private fun bindWidgets(view: View) {
        testList = view.findViewById(R.id.test_list)
    }

    private fun prepareRecyclerView() {
        val ref = Firebase.database.reference.child("/tests/")
        ref.keepSynced(true)
        val query =
            ref.orderByChild("status").equalTo("In Queue")
        val options = FirebaseRecyclerOptions.Builder<Test>()
            .setQuery(query, Test::class.java)
            .build()

        val adapter = PatientAdapter(activity!!, options, this)
        testList.adapter = adapter
        adapter.startListening()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DoctorFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DoctorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onTestClick(test: Test) {
        Paper.book().write("test", test)
        activity!!.startActivity(Intent(activity, CreatePrescriptionActivity::class.java))
    }
}