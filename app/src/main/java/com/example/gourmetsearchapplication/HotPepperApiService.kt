package com.example.gourmetsearchapplication

import retrofit2.http.GET
import retrofit2.http.Query

interface HotPepperApiService {
    @GET("hotpepper/gourmet/v1/")
    suspend fun searchShops(
        @Query("key") apiKey: String,
        @Query("keyword") keyword: String,
        @Query("address") address: String? = null, // 住所検索を強化
        @Query("start") start: Int? = null,      // ページネーション開始位置
        @Query("count") count: Int = 20,          // 1ページあたりの件数
        @Query("format") format: String = "json"
    ): HotPepperResponse
}