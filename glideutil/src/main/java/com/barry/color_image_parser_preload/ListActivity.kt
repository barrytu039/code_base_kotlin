package com.barry.color_image_parser_preload

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.barry.color_image_parser_preload.databinding.ActivityListBinding

class ListActivity : AppCompatActivity() {

    val viewModel : ListViewModel by viewModels()

    val listAdapter : NormalListAdapter by lazy {
        NormalListAdapter()
    }

    lateinit var binding : ActivityListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.colorListRecyclerView.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(this@ListActivity)
        }

        listAdapter.updateData(viewModel.getColorEntity())
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}