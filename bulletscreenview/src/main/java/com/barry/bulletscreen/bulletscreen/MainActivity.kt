package com.barry.bulletscreen.bulletscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.barry.bulletscreen.R
import com.barry.kotlin_code_base.bulletscreen.BulletScreenManager

class MainActivity : AppCompatActivity() {

    lateinit var bulletScreenView: BulletScreenView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bulletScreenView = findViewById(R.id.bulletScreenView)


    }

    override fun onResume() {
        super.onResume()
        val messageSet = mutableListOf<String>()
        for (i in 0 .. 100) {
            messageSet.add(i.toString())
        }
        BulletScreenManager.playMessage(bulletScreenView, messageSet, "10")
    }
}