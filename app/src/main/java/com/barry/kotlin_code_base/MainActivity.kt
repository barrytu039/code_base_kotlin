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

        GlobalScope.launch(Dispatchers.IO) {
            var apiManager = ApiManager()
            val resp  = apiManager.getAlbum()
            when (resp) {
                is BaseResultEntity.ApiSuccess<String> -> {
                    Log.e("data",Gson().toJson(resp))
                }
                is BaseResultEntity.ApiError -> {

                }
                is BaseResultEntity.ApiException -> {

                }
            }
        }
        for (i in 0 .. 100) {
            var message = i.toString() + ":"
            var time = (Math.random()* 10 + 1).toInt()
            for (i in 0 .. time) {
                message += "test"
            }

            messageList.add(message)
        }
        testview  = findViewById(R.id.testview)

        findViewById<Button>(R.id.button).setOnClickListener {
            if (isStarted) {
                BulletScreenManager.clear()
            } else {
                BulletScreenManager.playMessage(testview ,messageList,"test")
            }
            isStarted = !isStarted
        }
    }
}