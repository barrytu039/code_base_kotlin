package com.barry.color_image_parser_preload

import android.app.Application
import android.util.Log
import com.barry.kotlin_code_base.base.BaseViewModel
import com.barry.kotlin_code_base.network.BaseResultEntity


class ListViewModel(application: Application) : BaseViewModel(application) {

    var colorRepository = ColorRepository()

    fun getColorEntity() : List<ColorResultEntity> {
        return colorRepository.getAllColorEntity()
    }

}