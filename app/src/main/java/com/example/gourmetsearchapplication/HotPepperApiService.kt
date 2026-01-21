package com.example.gourmetsearchapplication

import retrofit2.http.GET
import retrofit2.http.Query

interface HotPepperApiService {
    @GET("hotpepper/gourmet/v1/")
    suspend fun searchShops(
        @Query("key") apiKey: String,
        @Query("keyword") keyword: String,
        @Query("format") format: String = "json",
        @Query("count") count: Int = 20
    ): HotPepperResponse
}