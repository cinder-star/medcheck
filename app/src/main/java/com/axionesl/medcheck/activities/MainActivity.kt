package com.axionesl.medcheck.activities

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.axionesl.medcheck.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage("Do you want to quit the app?")
            .setPositiveButton("Yes"
            ) { _, _ ->
                finishAffinity()
            }
            .setNegativeButton("no", null)
            .show()
    }
}