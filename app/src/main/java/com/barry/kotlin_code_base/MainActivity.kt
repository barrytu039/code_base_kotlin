package com.barry.kotlin_code_base

import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.barry.kotlin_code_base.base.BaseActivity
import com.barry.kotlin_code_base.bulletscreen.BulletScreenManager
import com.barry.kotlin_code_base.bulletscreen.BulletScreenView
import com.barry.kotlin_code_base.network.BaseResultEntity
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    var isStarted = false
    var messageList = mutableListOf<String>()
    lateinit var testview : BulletScreenView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}