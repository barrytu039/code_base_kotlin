package com.barry.kotlin_code_base

import android.util.Log
import com.barry.kotlin_code_base.network.BaseResultEntity
import com.barry.kotlin_code_base.network.RetrofitWorker
import com.google.gson.GsonBuilder
import kotlin.Exception

class ApiManager() {
    private val retrofitWorker : RetrofitWorker by lazy {
        RetrofitWorker()
    }

    suspend fun getAlbum() : BaseResultEntity<String> {
        return try {
            val headers = HashMap<String,String>()
            headers.put("Content-Type","application/json; charset=utf-8")
            headers.put("Authorization","Basic aGVhcnQ6Nk1aa2s1JWhHUk5wWFBZSiZoWnk=")
            val resp  = retrofitWorker.startGetRequest(header = headers, url = "http://demo2.doubleservice.com/heart/api/mindcare/v1/token", params = null)

            if (resp.code() == 200 || resp.code() == 201) {
                var stringResponse = resp.body()
                stringResponse?.apply {
                    return BaseResultEntity.ApiSuccess(this)
                }
                BaseResultEntity.ApiError(resp.code(),"error code")
            } else {
                BaseResultEntity.ApiError(resp.code(),"error code")
            }
        } catch (e : Exception) {
            BaseResultEntity.ApiException(e)
        }
    }
}