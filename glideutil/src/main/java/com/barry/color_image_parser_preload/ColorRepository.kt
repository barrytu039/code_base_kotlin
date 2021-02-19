package com.barry.color_image_parser_preload

import android.util.Log
import com.barry.kotlin_code_base.network.BaseResultEntity
import com.barry.kotlin_code_base.network.ContentType
import com.barry.kotlin_code_base.network.HeaderFactory
import com.barry.kotlin_code_base.network.RetrofitWorker
import com.google.gson.Gson

class ColorRepository {

    private val retrofitWorker : RetrofitWorker = RetrofitWorker()


    fun getAllColorEntity() : List<ColorResultEntity> {
        val dataSet = mutableListOf<ColorResultEntity>()
        dataSet.add(ColorResultEntity("https://homepages.cae.wisc.edu/~ece533/images/airplane.png"))
        dataSet.add(ColorResultEntity("https://homepages.cae.wisc.edu/~ece533/images/arctichare.png"))
        dataSet.add(ColorResultEntity("https://homepages.cae.wisc.edu/~ece533/images/baboon.png"))
        dataSet.add(ColorResultEntity("https://homepages.cae.wisc.edu/~ece533/images/boat.png"))

        return dataSet
    }

}