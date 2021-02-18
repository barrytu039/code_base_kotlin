package com.barry.appbarlayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import com.barry.appbarlayout.databinding.ActivityMainBinding
import com.google.android.material.appbar.AppBarLayout

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val displayMetrics  = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        Log.e("height",height.toString())
        val swipeLayoutParams = binding.swipeLayout.layoutParams
        swipeLayoutParams.height = height / 2
        binding.swipeLayout.layoutParams = swipeLayoutParams
        val collapsingLayoutParams = binding.collapsingToolBar.layoutParams as AppBarLayout.LayoutParams
        collapsingLayoutParams.topMargin = -(height / 2)
        binding.collapsingToolBar.layoutParams = collapsingLayoutParams
    }
}