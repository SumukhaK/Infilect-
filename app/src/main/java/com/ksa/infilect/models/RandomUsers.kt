package com.ksa.infilect.models


import com.google.gson.annotations.SerializedName

data class RandomUsers(
    @SerializedName("info")
    val info: Info,
    @SerializedName("results")
    val results: List<Result>
)