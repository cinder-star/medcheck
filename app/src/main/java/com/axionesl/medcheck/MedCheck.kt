package com.axionesl.medcheck

import android.app.Application
import io.paperdb.Paper

@Suppress("unused")
class MedCheck : Application() {
    override fun onCreate() {
        super.onCreate()
        Paper.init(this)
    }
}