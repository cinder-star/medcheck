package com.axionesl.medcheck.utils

import com.axionesl.medcheck.domains.Test

interface TestClickListener {
    fun onTestClick(test: Test)
}