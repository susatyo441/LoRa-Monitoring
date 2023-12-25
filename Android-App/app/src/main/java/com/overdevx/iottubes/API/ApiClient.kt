package com.overdevx.iottubes.API

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
   private const val BASE_URL = "https://susatyo441-4f0bb969-266c-468e-80c8-b833b4482fad.socketxp.com/"

    val retrofit: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }


}