package com.barry.color_image_parser_preload

import android.app.Application
import com.barry.kotlin_code_base.base.BaseViewModel

class PreLoadListViewModel(application: Application) : BaseViewModel(application) {

    var colorRepository = ColorRepository()

    suspend fun getColorEntity() : List<ColorResultEntity> {
        val dataSet = colorRepository.getAllColorEntity()
        dataSet.onEach {
            GlideUtil.downloadOnlySync(getApplication(), it.url)
        }
        return dataSet
    }
}