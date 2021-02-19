package com.barry.kotlin_code_base.network

class HeaderFactory {
    fun createHeader(contentType: ContentType) : Map<String, String> {
        var headerValue = HashMap<String, String>()
        if (contentType != ContentType.NONE) {
            headerValue["Content-Type"] = convertContentTypeString(contentType)
        }
        return headerValue
    }

    private fun convertContentTypeString(contentType: ContentType) : String {
        return when(contentType) {
            ContentType.Json -> "application/json; charset=utf-8"
            ContentType.UrlEncoded -> "application/x-www-form-urlencoded"
            ContentType.MultipartFormData -> "multipart/form-data;"
            else -> ""
        }
    }
}