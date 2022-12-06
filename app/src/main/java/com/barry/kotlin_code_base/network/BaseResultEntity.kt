package com.barry.kotlin_code_base.network

sealed class BaseResultEntity<out T>(code: Int) {
    data class ApiSuccess<out T>(val code:Int, val data: T) : BaseResultEntity<T>(code)
    data class ApiError(val code : Int,val msg: String) : BaseResultEntity<Nothing>(code)
    data class ApiException(val code: Int, val exception: Exception) : BaseResultEntity<Nothing>(code)
}