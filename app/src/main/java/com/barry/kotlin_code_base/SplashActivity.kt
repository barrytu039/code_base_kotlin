package com.barry.kotlin_code_base

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.barry.kotlin_code_base.base.BaseActivity

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }
}