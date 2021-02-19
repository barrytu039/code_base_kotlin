package com.barry.color_image_parser_preload

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.barry.color_image_parser_preload.databinding.ActivityPreLoadListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreLoadListActivity : AppCompatActivity() {

    lateinit var binding : ActivityPreLoadListBinding

    val viewModel : PreLoadListViewModel by viewModels()

    val listAdapter : PreLoadListAdapter by lazy {
        PreLoadListAdapter()
    }

    val linearLayoutManager : LinearLayoutManager by lazy {
        LinearLayoutManager(this)
    }

    var dialog : AlertDialog? =  null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreLoadListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        linearLayoutManager.initialPrefetchItemCount = 10
        binding.preloadListRecyclerView.apply {
            adapter = listAdapter
            layoutManager = linearLayoutManager
            setItemViewCacheSize(10)
        }
        dialog?.dismiss()
        dialog = showDialog(this@PreLoadListActivity)
        lifecycleScope.launch(Dispatchers.IO) {
           val dataSet =  viewModel.getColorEntity()
            withContext(Dispatchers.Main) {
                listAdapter.updateData(dataSet)
                dialog?.dismiss()
            }
        }
    }


    open fun showDialog(context: Context): AlertDialog {
        var dialog = AlertDialog.Builder(context).setView(R.layout.dialog_progress).setCancelable(true).create()
        dialog.show()
        return dialog
    }

    override fun onDestroy() {
        dialog?.dismiss()
        super.onDestroy()
    }
}