package com.barry.kotlin_code_base

import android.os.Bundle
import androidx.activity.viewModels
import com.barry.kotlin_code_base.base.BaseActivity

class SplashActivity : BaseActivity() {

    private val splashViewModel: SplashViewModel by viewModels {
        SplashViewModelFactory(this, application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }
}