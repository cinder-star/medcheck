package com.axionesl.medcheck.activities

import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.Test
import com.axionesl.medcheck.utils.AbstractTestDetailsActivity

class TestDetailsActivity : AbstractTestDetailsActivity(R.layout.activity_test_details) {
    private lateinit var prescription: TextView
    private var oldTest: Test? = null
    override fun bindWidgets() {
        super.bindWidgets()
        prescription = findViewById(R.id.test_prescription)
    }

    override fun updateUI(test: Test?) {
        super.updateUI(test)
        oldTest = test
        prescription.text = test!!.prescription
    }

    override fun bindListeners() {
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.test_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_test -> {
                startActivity(Intent(this, AddTestActivity::class.java).putExtra("test", oldTest))
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}