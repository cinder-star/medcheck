package com.axionesl.medcheck.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.axionesl.medcheck.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.paperdb.Paper

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (checkUser()) {
            val type: String = Paper.book().read<String>("account_type", null)
            if (type == "Patient"){
                changeActivity(PatientActivity::class.java)
            } else {
                changeActivity(MainActivity::class.java)
            }
        } else {
            changeActivity(LoginActivity::class.java)
        }
    }

    private fun checkUser(): Boolean {
        if (Firebase.auth.currentUser == null) return false
        return true
    }

    private fun <T> changeActivity(jClass: Class<T>) {
        val i = Intent(this, jClass)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }
}