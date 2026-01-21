package com.example.gourmetsearchapplication

import com.google.gson.annotations.SerializedName

// APIの階層構造（results > shop > リスト）をそのままクラスにします
data class HotPepperResponse(
    val results: Results
)

data class Results(
    @SerializedName("shop") val shops: List<ShopResponse>
)

// APIから返ってくる1店舗あたりの情報
data class ShopResponse(
    val name: String,
    @SerializedName("catch") val catchCopy: String,
    @SerializedName("photo") val photo: Photo,
    val genre: Genre,
    val address: String,
    val access: String
)

data class Photo(val pc: PcPhoto)
data class PcPhoto(@SerializedName("l") val large: String)
data class Genre(val name: String)