package com.barry.color_image_parser_preload

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.barry.color_image_parser_preload.databinding.ActivityMainBinding
import com.barry.kotlin_code_base.base.BaseActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.normalTitleTextView.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
        }

        binding.preLoadTitleTextView.setOnClickListener {
            val intent = Intent(this, PreLoadListActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
//        Glide.get(application).clearMemory()
        GlobalScope.launch(Dispatchers.IO) {
//            Glide.get(application).clearDiskCache()
            Log.e("data","clear")
        }
    }
}