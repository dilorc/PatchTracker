package com.example.patchtracker.nightscout

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Nightscout API interface.
 */
interface NightscoutApi {

    @POST("/api/v1/treatments")
    suspend fun postTreatment(
        @Header("api-secret") apiSecret: String,
        @Body treatment: NightscoutTreatment
    ): Response<List<NightscoutResponse>>
}

