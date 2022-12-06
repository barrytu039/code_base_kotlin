package com.barry.kotlin_code_base.network

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface RetrofitService {
    @GET
    suspend fun get(
        @HeaderMap headers: Map<String, String>,
        @Url url: String
    ): Response<String>

    @GET
    suspend fun get(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @QueryMap params: Map<String, String>
    ): Response<String>

    @POST
    suspend fun post(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @Body requestBody: RequestBody
    ): Response<String>

    @FormUrlEncoded
    @POST
    suspend fun post(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @FieldMap fiedlMap: Map<String, String>
    ): Response<String>

    @PUT
    suspend fun put(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @Body requestBody: RequestBody
    ): Response<String>

    @FormUrlEncoded
    @PUT
    suspend fun put(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @FieldMap requestBody: Map<String, String>
    ): Response<String>

    @HTTP(method = "DELETE", hasBody = true)
    suspend fun delete(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @Body requestBody: RequestBody
    ): Response<String>

    @FormUrlEncoded
    @HTTP(method = "DELETE", hasBody = true)
    suspend fun delete(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @FieldMap requestBody: Map<String, String>
    ): Response<String>

    @PATCH
    suspend fun patch(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @Body requestBody: RequestBody
    ): Response<String>
}