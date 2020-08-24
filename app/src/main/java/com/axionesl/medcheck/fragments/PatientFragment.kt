package com.axionesl.medcheck.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.axionesl.medcheck.R
import com.axionesl.medcheck.activities.AddTestActivity
import com.axionesl.medcheck.activities.TestDetailsActivity
import com.axionesl.medcheck.domains.Test
import com.axionesl.medcheck.utils.PatientAdapter
import com.axionesl.medcheck.utils.TestClickListener
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.paperdb.Paper

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PatientFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PatientFragment : Fragment(), TestClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var addTest: FloatingActionButton
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
        val view = inflater.inflate(R.layout.fragment_patient, container, false)
        bindWidgets(view)
        bindListeners()
        updateRecyclerView()
        return view
    }

    private fun updateRecyclerView() {
        testList.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        prepareRecyclerView()
    }

    private fun bindWidgets(view: View) {
        addTest = view.findViewById(R.id.add_test)
        testList = view.findViewById(R.id.test_list)
    }

    private fun bindListeners() {
        addTest.setOnClickListener {
            activity!!.startActivity(Intent(activity, AddTestActivity::class.java))
        }
    }

    private fun prepareRecyclerView() {
        val uid = Firebase.auth.currentUser!!.uid
        val ref = Firebase.database.reference.child("/tests/")
        ref.keepSynced(true)
        val query =
            ref.orderByChild("patient").equalTo(uid)
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
         * @return A new instance of fragment PatientFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PatientFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onTestClick(test: Test) {
        Paper.book().write("test_id", test.id)
        activity!!.startActivity(Intent(activity, TestDetailsActivity::class.java))
    }
}