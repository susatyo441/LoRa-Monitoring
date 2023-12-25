package com.overdevx.iottubes.API

import com.overdevx.iottubes.getData.dataResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @Headers(
        "Content-Type: application/json",
        "Host: susatyo441-4f0bb969-266c-468e-80c8-b833b4482fad.socketxp.com",
        "User-Agent: PostmanRuntime/7.36.0",
        "Accept: */*",
        "Accept-Encoding: gzip, define, br",
        "Connection: keep-alive"
    )
    @POST("predict")
    suspend fun getPredict(
        @Body request: RequestBody
    ): Response<dataResponse>
}