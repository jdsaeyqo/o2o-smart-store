package com.ssafy.smartstore.service

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface FirebaseTokenService {
    // Token 정보 서버로 전송
    @POST("token")
    fun uploadToken(@Query("token") token: String): Call<String>
}