package com.ksa.infilect.network

import com.ksa.infilect.models.RandomUsers
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomUsersApi {
    @GET("?")
    suspend fun getRandomUsers(@Query("results") noOfResults:String): Response<RandomUsers>

}