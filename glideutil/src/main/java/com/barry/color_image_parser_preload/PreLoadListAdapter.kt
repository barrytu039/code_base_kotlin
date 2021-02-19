package com.barry.color_image_parser_preload

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PreLoadListAdapter : RecyclerView.Adapter<PreLoadListAdapter.ViewHolder>() {

    var dataSet = mutableListOf<ColorResultEntity>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView by lazy {
            itemView.findViewById<ImageView>(R.id.itemImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.image_itemview, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        GlideUtil.preload(dataSet[position].url, holder.imageView)
    }

    fun updateData(newDataSet: List<ColorResultEntity>) {
        dataSet.clear()
        dataSet.addAll(newDataSet.toMutableList())
        notifyDataSetChanged()
    }
}