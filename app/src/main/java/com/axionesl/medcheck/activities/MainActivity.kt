package com.axionesl.medcheck.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.axionesl.medcheck.R
import com.axionesl.medcheck.domains.User
import com.axionesl.medcheck.fragments.DoctorFragment
import com.axionesl.medcheck.fragments.PatientFragment
import io.paperdb.Paper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Tests"
        loadView()
    }

    private fun loadView() {
        val user = Paper.book().read<User>("user", null)
        if (user.accountType == "Patient") {
            loadFragment(PatientFragment())
        } else {
            loadFragment(DoctorFragment())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val manager = supportFragmentManager.beginTransaction()
        manager.replace(R.id.fragment_container, fragment)
        manager.addToBackStack(null)
        manager.commit()
    }


}